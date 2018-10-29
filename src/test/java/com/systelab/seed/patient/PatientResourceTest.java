package com.systelab.seed.patient;

import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.patient.entity.Address;
import com.systelab.seed.patient.entity.Patient;
import com.systelab.seed.patient.entity.PatientsPage;
import com.systelab.seed.utils.FakeNameGenerator;
import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.joining;

@TmsLink("TC0001_PatientManagement_IntegrationTest")
@Feature("Patient Test Suite.\n\nGoal:\nThe goal of this TC is to verify that the Patient management actions (CRUD) behabe as expected according the specifications and the input values.\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class PatientResourceTest extends RESTResourceTest {

    private Patient getPatientData() {
        Patient patient = new Patient();
        patient.setName("Ralph");
        patient.setSurname("Burrows");
        patient.setEmail("rburrows@gmail.com");
		//These are optional
        Address address = new Address();
        address.setStreet("E-Street, 90");
        address.setCity("Barcelona");
        address.setZip("08021");
        patient.setAddress(address);
        return patient;
    }
	
    private Patient getPatientData(String name, String surname, String email) {
        Patient patient = getPatientData();
        patient.setName(name);
        patient.setSurname(surname);
        patient.setEmail(email);
        return patient;
    }

    @Description("Create a new patient with name, surname and email")
    @Test
    public void testCreatePatient() {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(patient).
                when().post("/patients/patient").as(Patient.class);
        TestUtil.checkObjectIsNotNull("Patient", patientCreated);
        TestUtil.checkField("Name", "John", patientCreated.getName());
        TestUtil.checkField("Surname", "Burrows", patientCreated.getSurname());
        TestUtil.checkField("Email", "jburrows@werfen.com", patientCreated.getEmail());
    }

    private void testCreateInvalidPatient(Patient patient) {
    	
        RequestSpecification httpRequest =given();
        httpRequest.contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(patient);
        Response response = httpRequest.post("/patients/patient");
        int statusCode =response.getStatusCode();
		TestUtil.checkField("Status Code", 400, statusCode);
    }

	@Description("Create a Patient with invalid data")
    @Test
    public void testCreateInvalidPatient() {
        testCreateInvalidPatient(getPatientData("", "Burrows", "jburrows@test.com"));
        testCreateInvalidPatient(getPatientData("John", "", "jburrows@test.com"));
        testCreateInvalidPatient(getPatientData("", "", "jburrows@test.com"));
        testCreateInvalidPatient(getPatientData("John", "Burrows", "jburrows"));
    }

    @Attachment(value = "Patients Database")
    private String savePatientsDatabase(List<Patient> patients) {
        return patients.stream().map((patient) -> patient.getSurname() + ", " + patient.getName() + "\t" + patient.getEmail()).collect(joining("\n"));
    }

    @Step("Action: Create {0} patients")
    private void createSomePatients(int numberOfPatients) {
        FakeNameGenerator aFakeNameGenerator = new FakeNameGenerator();
        for (int i = 0; i < numberOfPatients; i++) {
            Patient patient = getPatientData(aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(false) + "@werfen.com");
            Patient patientCreated = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(patient).
                    when().post("/patients/patient").as(Patient.class);
            //This check is identical for all patients and shall be specific or merged in one informing about creation of {0} patients.
			TestUtil.checkObjectIsNotNull("Patient ID "+patientCreated.getId(), patientCreated.getId());
        }
    }

    @Description("Get a list of patients")
    @Test
    public void testGetPatientList() {
    	
    	int numberOfPatients=5;
        createSomePatients(numberOfPatients);

        PatientsPage patientsBefore = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().get("/patients").as(PatientsPage.class);
        long initialSize = patientsBefore.getTotalElements();
        savePatientsDatabase(patientsBefore.getContent());
		TestUtil.checkANumber("List size", numberOfPatients, initialSize);
		createSomePatients(numberOfPatients);

        PatientsPage patientsAfter = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().get("/patients").as(PatientsPage.class);
        long finalSize = patientsAfter.getTotalElements();
        savePatientsDatabase(patientsAfter.getContent());
        TestUtil.checkANumber("List size", initialSize + numberOfPatients, finalSize);
    }

    @Description("Get a Patient by id")
    @Test
    public void testGetPatient() {

        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(patient).
                when().post("/patients/patient").as(Patient.class);
        //I would assign ID and use the value to report in the exp.result
		TestUtil.checkObjectIsNotNull("Patient ID "+patientCreated.getId(), patientCreated.getId());
        Patient patientRetrieved = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().get("/patients/" + patientCreated.getId()).as(Patient.class);
        TestUtil.checkObjectIsNotNull("Patient", patientRetrieved);
        if(patientRetrieved!=null){
			TestUtil.checkField("Name", "John", patientRetrieved.getName());
	        TestUtil.checkField("Surname", "Burrows", patientRetrieved.getSurname());
	        TestUtil.checkField("Email", "jburrows@werfen.com", patientRetrieved.getEmail());
        }
    }

    @Description("Get a patient with an non-existing id")
    @Test
    public void testGetUnexistingPatient() {
	     RequestSpecification httpRequest = given();
	   	 httpRequest.contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer());
	   	 Response response = httpRequest.get("/patients/38400000-8cf0-11bd-b23e-10b96e4ef00d");
	   	 int statusCode =response.getStatusCode();
	   	 TestUtil.checkField("Status Code after a GET", 404, statusCode);
    }

    @Description("Delete a patient by id")
    @Test
    public void testDeletePatient() {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(patient).
                when().post("/patients/patient").as(Patient.class);
        //We don't need to log this check
		//TestUtil.checkObjectIsNotNull("Patient", patientCreated);
		Assertions.assertNotNull(patientCreated);
        given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().delete("/patients/" + patientCreated.getId()).
                then().statusCode(200);

        RequestSpecification httpRequest =given();
        httpRequest.contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer());
        Response response = httpRequest.get("/patients/" + patientCreated.getId());
        int statusCode =response.getStatusCode();
		TestUtil.checkField("Status Code after a GET", 404, statusCode);
    }

    @Description("Delete non-existing Patient")
    @Test
    public void testDeleteUnexistingPatient() {
    	 RequestSpecification httpRequest = given();
    	 httpRequest.contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer());
    	 Response response = httpRequest.delete("/patients/38400000-8cf0-11bd-b23e-10b96e4ef00d");
    	 int statusCode =response.getStatusCode();
  		 TestUtil.checkField("Status Code", 404, statusCode);
    }
}