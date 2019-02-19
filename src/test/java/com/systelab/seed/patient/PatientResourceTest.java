package com.systelab.seed.patient;

import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.patient.entity.Address;
import com.systelab.seed.patient.entity.Patient;
import com.systelab.seed.patient.entity.PatientsPage;
import com.systelab.seed.utils.FakeNameGenerator;
import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

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
        patient.setMedicalNumber("123456");
        patient.setDob(LocalDate.of(1948, 8, 11));
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

    private Patient getPatientData(String name, String surname, String email, String medicalNumber) {
        Patient patient = getPatientData();
        patient.setName(name);
        patient.setSurname(surname);
        patient.setEmail(email);
        patient.setMedicalNumber(medicalNumber);
        return patient;
    }

    @Description("Create a new patient with name, surname, email and medical number")
    @Test
    public void testCreatePatient() {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com", "112233");
        Patient patientCreated = given().body(patient)
                .when().post("/patients/patient")
                .then().assertThat().statusCode(200)
                .extract().as(Patient.class);
        TestUtil.checkObjectIsNotNull("Patient", patientCreated);
        TestUtil.checkField("Name", "John", patientCreated.getName());
        TestUtil.checkField("Surname", "Burrows", patientCreated.getSurname());
        TestUtil.checkField("Email", "jburrows@werfen.com", patientCreated.getEmail());
        // include validation all patient fields
        TestUtil.checkField("Medical Number", "112233", patientCreated.getMedicalNumber());
        TestUtil.checkField("DOB", LocalDate.of(1948, 8, 11), patientCreated.getDob());
        TestUtil.checkField("Street", "E-Street, 90", patientCreated.getAddress().getStreet());
        TestUtil.checkField("City", "Barcelona", patientCreated.getAddress().getCity());
        TestUtil.checkField("Zip", "08021", patientCreated.getAddress().getZip());
    }

    private void testCreateInvalidPatient(Patient patient) {

        int statusCode = given().body(patient)
                .when().post("/patients/patient")
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code", 400, statusCode);
    }

    @Description("Create a Patient with invalid data")
    @Test
    public void testCreateInvalidPatient() {
        testCreateInvalidPatient(getPatientData("", "", "", ""));
        testCreateInvalidPatient(getPatientData("", "", "", "111"));
        testCreateInvalidPatient(getPatientData("", "", "jburrows@example", ""));
        testCreateInvalidPatient(getPatientData("", "", "jburrows@.com", "222"));
        testCreateInvalidPatient(getPatientData("", "Jameson", "", ""));
        testCreateInvalidPatient(getPatientData("", "Jameson", "", "111"));
        testCreateInvalidPatient(getPatientData("", "Jameson", "@example.com", ""));
        testCreateInvalidPatient(getPatientData("", "Jameson", "jj@.", "333"));
        testCreateInvalidPatient(getPatientData("Thomas", "", "", ""));
        testCreateInvalidPatient(getPatientData("Thomas", "", "", "111"));
        testCreateInvalidPatient(getPatientData("Thomas", "", "jj@example.com", ""));
        testCreateInvalidPatient(getPatientData("John", "", "jburrows@test,com", "222"));
        testCreateInvalidPatient(getPatientData("Thomas", "Summers", "", ""));
        testCreateInvalidPatient(getPatientData("Thomas", "Summers", "", "333"));
        testCreateInvalidPatient(getPatientData("Thomas", "Summers", "tsms@example,com", ""));
        testCreateInvalidPatient(getPatientData("John", "Burrows", "jburrows,", "345423"));
        //TODO: All possible combinations and patterns(email)
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
            Patient patientCreated = given().body(patient)
                    .when().post("/patients/patient")
                    .then().assertThat().statusCode(200)
                    .extract().as(Patient.class);
            //This check is identical for all patients and shall be specific or merged in one informing about creation of {0} patients.
            TestUtil.checkObjectIsNotNull("Patient ID " + patientCreated.getId(), patientCreated.getId());
        }
    }

    @Description("Get a list of patients")
    @Test
    public void testGetPatientList() {

        int numberOfPatients = 5;
        createSomePatients(numberOfPatients);

        PatientsPage patientsBefore = given()
                .when().get("/patients")
                .then().assertThat().statusCode(200)
                .extract().as(PatientsPage.class);
        long initialSize = patientsBefore.getTotalElements();
        savePatientsDatabase(patientsBefore.getContent());
        TestUtil.checkANumber("List size", numberOfPatients, initialSize);
        // TODO: Verify the elements in the list also.
        createSomePatients(numberOfPatients);

        PatientsPage patientsAfter = given()
                .when().get("/patients")
                .then().assertThat().statusCode(200)
                .extract().as(PatientsPage.class);
        long finalSize = patientsAfter.getTotalElements();
        savePatientsDatabase(patientsAfter.getContent());
        TestUtil.checkANumber("List size", initialSize + numberOfPatients, finalSize);
    }

    @Description("Get a Patient by id")
    @Test
    public void testGetPatient() {

        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().body(patient)
                .when().post("/patients/patient")
                .then().assertThat().statusCode(200)
                .extract().as(Patient.class);

        TestUtil.checkObjectIsNotNull("Patient ID " + patientCreated.getId(), patientCreated.getId());
        Patient patientRetrieved = given()
                .when().get("/patients/" + patientCreated.getId())
                .then().assertThat().statusCode(200)
                .extract().as(Patient.class);
        TestUtil.checkObjectIsNotNull("Patient", patientRetrieved);
        if (patientRetrieved != null) {
            TestUtil.checkField("Name", "John", patientRetrieved.getName());
            TestUtil.checkField("Surname", "Burrows", patientRetrieved.getSurname());
            TestUtil.checkField("Email", "jburrows@werfen.com", patientRetrieved.getEmail());

            TestUtil.checkField("Medical Number", "123456", patientRetrieved.getMedicalNumber());
            TestUtil.checkField("DOB", LocalDate.of(1948, 8, 11), patientRetrieved.getDob());
        }
    }

    @Description("Get a patient with an non-existing id")
    @Test
    public void testGetUnexistingPatient() {
        int statusCode = given()
                .when().get("/patients/38400000-8cf0-11bd-b23e-10b96e4ef00d")
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 404, statusCode);
    }

    @Description("Delete a patient by id")
    @Test
    public void testDeletePatient() {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().body(patient)
                .when().post("/patients/patient")
                .then().assertThat().statusCode(200)
                .extract().as(Patient.class);

        Assertions.assertNotNull(patientCreated);
        given()
                .when().delete("/patients/" + patientCreated.getId())
                .then().assertThat().statusCode(200);

        int statusCode = given()
                .when().get("/patients/" + patientCreated.getId())
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 404, statusCode);
    }

    @Description("Delete non-existing Patient")
    @Test
    public void testDeleteUnexistingPatient() {
        int statusCode = given()
                .when().delete("/patients/38400000-8cf0-11bd-b23e-10b96e4ef00d")
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code", 404, statusCode);
    }

    @Description("Update a patient by id")
    @Test
    public void testUpdatePatient()
    {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().body(patient)
            .when().post("/patients/patient")
            .then().assertThat().statusCode(200)
            .extract().as(Patient.class);
        Assertions.assertNotNull(patientCreated);
        patientCreated.setEmail("new@emailchanged.com");
        Patient patientUpdated = given().body(patientCreated)
            .when().put("/patients/" + patientCreated.getId())
            .then().assertThat().statusCode(200)
            .extract().as(Patient.class);
        TestUtil.checkObjectIsNotNull("Patient", patientUpdated);
        //TestUtil.checkField("Id", patientCreated.getId(), patientUpdated.getId());
        TestUtil.checkField("Email", patientCreated.getEmail(), patientUpdated.getEmail());
        TestUtil.checkField("Name", patientCreated.getName(), patientUpdated.getName());
        TestUtil.checkField("Surname", patientCreated.getSurname(), patientUpdated.getSurname());
        TestUtil.checkField("Medical Number", patientCreated.getMedicalNumber(), patientUpdated.getMedicalNumber());
        TestUtil.checkField("DoB", patientCreated.getDob(), patientUpdated.getDob());
        TestUtil.checkField("Street", patientCreated.getAddress().getStreet(), patientUpdated.getAddress().getStreet());
        TestUtil.checkField("City", patientCreated.getAddress().getCity(), patientUpdated.getAddress().getCity());
        TestUtil.checkField("Zip", patientCreated.getAddress().getZip(), patientUpdated.getAddress().getZip());
    }

    @Description("Update non-existent patient, that is Create new Patient")
    @Test
    public void testUpdateUnexistingPatient()
    {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = given().body(patient)
            .when().put("/patients/38400000-8cf0-11bd-b23e-10b96e4ef00d")
            .then()
            .extract().as(Patient.class);
        TestUtil.checkObjectIsNotNull("Patient", patientCreated);
        //TestUtil.checkField("Id", patientCreated.getId(), patient.getId());
        TestUtil.checkField("Email", patientCreated.getEmail(), patient.getEmail());
        TestUtil.checkField("Name", patientCreated.getName(), patient.getName());
        TestUtil.checkField("Surname", patientCreated.getSurname(), patient.getSurname());
        TestUtil.checkField("Medical Number", patientCreated.getMedicalNumber(), patient.getMedicalNumber());
        //TestUtil.checkField("DoB", patientCreated.getDob(), patient.getDob());
        TestUtil.checkField("Street", patientCreated.getAddress().getStreet(), patient.getAddress().getStreet());
        TestUtil.checkField("City", patientCreated.getAddress().getCity(), patient.getAddress().getCity());
        TestUtil.checkField("Zip", patientCreated.getAddress().getZip(), patient.getAddress().getZip());
    }
}