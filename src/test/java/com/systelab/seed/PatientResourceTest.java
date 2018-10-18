package com.systelab.seed;

import com.systelab.seed.model.patient.Address;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.model.patient.PatientsPage;
import com.systelab.seed.utils.FakeNameGenerator;
import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.joining;

@TmsLink("TC0001_PatientManagement_IntegrationTest")
@Feature("Patient Test Suite.\n\nGoal:\nThis test case is intended to verify the correct ....\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class PatientResourceTest extends RESTResourcelTest {

    private Patient getPatientData() {
        Patient patient = new Patient();
        patient.setName("Ralph");
        patient.setSurname("Burrows");
        patient.setEmail("rburrows@gmail.com");

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

    @Description("Create a patient with name, surname and email")
    @Test
    public void testCreatePatient() {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(patient).
                when().post("/patients/patient").as(Patient.class);
        TestUtil.checkObjectIsNotNull("patient", patientCreated);
        TestUtil.checkField("Name", "John", patientCreated.getName());
        TestUtil.checkField("Surname", "Burrows", patientCreated.getSurname());
        TestUtil.checkField("Email", "jburrows@werfen.com", patientCreated.getEmail());
    }

    private void testCreateInvalidPatient(Patient patient) {
        given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(patient).
                when().post("/patients/patient").
                then().statusCode(400);
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
            TestUtil.checkObjectIsNotNull("Patient ID", patientCreated.getId());
        }
    }

    @Description("Get a list of patients")
    @Test
    public void testGetPatientList() {

        createSomePatients(5);

        PatientsPage patientsBefore = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().get("/patients").as(PatientsPage.class);
        long initialSize = patientsBefore.getTotalElements();
        savePatientsDatabase(patientsBefore.getContent());
        createSomePatients(5);

        PatientsPage patientsAfter = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().get("/patients").as(PatientsPage.class);
        long finalSize = patientsAfter.getTotalElements();
        savePatientsDatabase(patientsAfter.getContent());

        TestUtil.checkANumber("The new list size is", initialSize + 5, finalSize);
    }

    @Description("Get a Patient by id")
    @Test
    public void testGetPatient() {

        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(patient).
                when().post("/patients/patient").as(Patient.class);
        TestUtil.checkObjectIsNotNull("Patient ID", patientCreated.getId());
        Patient patientRetrieved = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().get("/patients/" + patientCreated.getId()).as(Patient.class);
        TestUtil.checkObjectIsNotNull("patient", patientRetrieved);
        TestUtil.checkField("Name", "John", patientRetrieved.getName());
        TestUtil.checkField("Surname", "Burrows", patientRetrieved.getSurname());
        TestUtil.checkField("Email", "jburrows@werfen.com", patientRetrieved.getEmail());
    }

    @Description("Get a patient with an non-existing id")
    @Test
    public void testGetUnexistingPatient() {
        given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().get("/patients/38400000-8cf0-11bd-b23e-10b96e4ef00d").
                then().statusCode(404);
    }

    @Description("Delete a patient by id")
    @Test
    public void testDeletePatient() {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(patient).
                when().post("/patients/patient").as(Patient.class);
        TestUtil.checkObjectIsNotNull("patient", patientCreated);
        given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().delete("/patients/" + patientCreated.getId()).
                then().statusCode(200);
    }

    @Description("Delete non-existing Patient")
    @Test
    public void testDeleteUnexistingPatient() {
        given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().delete("/patients/38400000-8cf0-11bd-b23e-10b96e4ef00d").
                then().statusCode(404);
    }
}