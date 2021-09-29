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
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.joining;
import io.restassured.response.Response;

@TmsLink("TC0001_PatientManagement_IntegrationTest")
@Feature("Patient Test Suite.\n\nGoal:\nThe goal of this TC is to verify that the Patient management actions (CRUD) behave as expected according the specifications and the input values.\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class PatientResourceTest extends RESTResourceTest {

    private Response doCreatePatient(Patient patient){ return given().body(patient).when().post("/patients/patient"); }

    private Response doUpdatePatient(Patient patientUpdated, UUID patientCreatedId)  { return given().body(patientUpdated).when().put("/patients/" + patientCreatedId); }

    private Response doDeletePatient(UUID patientCreatedId){ return given().when().delete("/patients/" + patientCreatedId); }

    private Response doGetPatient(UUID patientGetId){
        return given().when().get("/patients/" + patientGetId);
    }

    private Response doGetPatientList(){ return given().when().get("/patients/"); }


    private Patient getPatientData(String name, String surname, String email, String medicalNumber, LocalDate dob, String street, String city, String zip) {
        Patient patient = new Patient();

        patient.setName(name);
        patient.setSurname(surname);
        patient.setEmail(email);
        patient.setMedicalNumber(medicalNumber);
        patient.setDob(dob);
        patient.setAddress(new Address());
        patient.getAddress().setStreet(street);
        patient.getAddress().setCity(city);
        patient.getAddress().setZip(zip);
        return patient;
    }

    private void checkPatientData(Patient expected, Patient actual){

        TestUtil.checkObjectIsNotNull("Patient", actual);
        TestUtil.checkField("Name", expected.getName(), actual.getName());
        TestUtil.checkField("Surname", expected.getSurname(), actual.getSurname());
        if (expected.getEmail() != null)
            TestUtil.checkField("Email", expected.getEmail(), actual.getEmail());
        if (expected.getMedicalNumber() != null)
            TestUtil.checkField("Medical Number", expected.getMedicalNumber(), actual.getMedicalNumber());
        if (expected.getDob() != null)
            TestUtil.checkField("DOB", expected.getDob(), actual.getDob());
        if (expected.getAddress() != null){
            if (expected.getAddress().getStreet() != null)
                TestUtil.checkField("Street", expected.getAddress().getStreet(), actual.getAddress().getStreet());
            if (expected.getAddress().getCity() != null)
                TestUtil.checkField("City", expected.getAddress().getCity(), actual.getAddress().getCity());
            if (expected.getAddress().getZip() != null)
                TestUtil.checkField("Zip", expected.getAddress().getZip(), actual.getAddress().getZip());
        }
    }

    @Description("Create a new patient with name, surname, email and medical number")
    @Test
    public void testCreatePatient() {
        String expectedName = "John";
        String expectedSurname = "Burrows";
        String expectedEmail = "jburrows@werfen.com";
        String expectedMedNumber = "112233";
        LocalDate expectedDob = null;
        String expectedStreet = null;
        String expectedCity = null;
        String expectedZip = null;
        Patient patient = getPatientData(expectedName, expectedSurname, expectedEmail, expectedMedNumber, expectedDob , expectedStreet, expectedCity, expectedZip);

        Response response = doCreatePatient(patient);
        Patient patientCreated = response.then().extract().as(Patient.class);
        int status = response.then().extract().statusCode();
        TestUtil.checkField("Status Code", 200, status);
        checkPatientData(patient, patientCreated);

    }

    @Description("Create a new patient with name, surname, email, medical number, dob, and complete address")
    @Test
    public void testCreatePatientWithAllInfo() {
        String expectedName = "Jane";
        String expectedSurname = "Senfield";
        String expectedEmail = "jsenfield@example.com";
        String expectedMedNumber = "123123";
        LocalDate expectedDob = LocalDate.of(1948, 8, 12);
        String expectedStreet = "5 Elm St.";
        String expectedCity = "Madrid";
        String expectedZip = "28084";
        Patient patient = getPatientData(expectedName, expectedSurname, expectedEmail, expectedMedNumber, expectedDob, expectedStreet, expectedCity, expectedZip);

        Response response = doCreatePatient(patient);
        Patient patientCreated = response.then().extract().as(Patient.class);
        int status = response.then().extract().statusCode();
        TestUtil.checkField("Status Code", 200, status);
        checkPatientData(patient, patientCreated);
    }

    private void testCreateInvalidPatient(Patient patient) {

        int statusCode = given().body(patient)
                .when().post("/patients/patient")
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code", 400, statusCode);
    }

    @Description("Create a Patient with invalid data: mandatory fields empty (name, surname)")
    @Test
    public void testCreateInvalidPatientMandatoryFieldsEmpty() {
        testCreateInvalidPatient(getPatientData("", "", "", "", null, null,null,null));
        testCreateInvalidPatient(getPatientData("", "Jameson", "jj@.", "333",null, null,null,null));
        testCreateInvalidPatient(getPatientData("John", "", "jburrows@test,com", "222",null, null,null,null));
    }

    @Description("Create a Patient with invalid data: email format")
    @Test
    public void testCreateInvalidPatientEmailFormat() {
        testCreateInvalidPatient(getPatientData("John", "Jameson", "@test.com", "222",null, null,null,null));
        testCreateInvalidPatient(getPatientData("John", "Jameson", "user@", "222",null, null,null,null));
        testCreateInvalidPatient(getPatientData("John", "Jameson", "@.com", "222",null, null,null,null));
        testCreateInvalidPatient(getPatientData("John", "Jameson", "@.", "222",null, null,null,null));
        testCreateInvalidPatient(getPatientData("John", "Jameson", ".", "222",null, null,null,null));
        testCreateInvalidPatient(getPatientData("John", "Jameson", "@", "222",null, null,null,null));
    }

    @Description("Create a Patient with invalid data: name, surname or medicalNumber field too long")
    @Test
    public void testCreateInvalidPatientTooLongText() {
        String tooLongString = "thisStringIsIntendedToCauseAnExceptionBecauseOfItsExcessiveLengthTheMostLongStringAllowedMustHaveLessThanTeoHundredAndFiftyFiveCharactersThisShouldBeVerifiedInEveryTextFieldToEnsureTheLimitationIsWorkingProperlyThisStringOnlyHasEnglishLettersButMoreScenarios";

        testCreateInvalidPatient(getPatientData(tooLongString, "Jameson", "jj@test.com", "123",null, null,null,null));
        testCreateInvalidPatient(getPatientData("John", tooLongString, "jj@test.com", "123",null, null,null,null));
        testCreateInvalidPatient(getPatientData("John", "Jameson", "jj@test.com", tooLongString,null, null,null,null));
    }

    @Attachment(value = "Patients Database")
    private String savePatientsDatabase(List<Patient> patients) {
        return patients.stream().map((patient) -> patient.getSurname() + ", " + patient.getName() + "\t" + patient.getEmail()).collect(joining("\n"));
    }

    @Step("Action: Create {0} patients")
    private void createSomePatients(int numberOfPatients) {
        FakeNameGenerator aFakeNameGenerator = new FakeNameGenerator();
        for (int i = 0; i < numberOfPatients; i++) {
            Patient patient = getPatientData(aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(false) + "@werfen.com",null, null,null,null, null);
            Response responseCre = doCreatePatient(patient);
            Patient patientCreated = responseCre.then().assertThat().statusCode(200).extract().as(Patient.class);

            Assertions.assertNotNull(patientCreated.getId(), "Patient not created");
        }
    }

    @Description("Get a list of patients")
    @Test
    public void testGetPatientList() {

        int numberOfPatients = 5;

        Response responseBefore = doGetPatientList();

        PatientsPage patientsBefore = responseBefore.then().assertThat().statusCode(200).extract().as(PatientsPage.class);
        long initialSize = patientsBefore.getTotalElements();
        savePatientsDatabase(patientsBefore.getContent());

        createSomePatients(numberOfPatients);

        Response responseAfter = doGetPatientList();
        PatientsPage patientsAfter = responseAfter.then().assertThat().statusCode(200).extract().as(PatientsPage.class);
        long finalSize = patientsAfter.getTotalElements();
        savePatientsDatabase(patientsAfter.getContent());

        TestUtil.checkANumber("List size", initialSize + numberOfPatients, finalSize);
    }


    @Description("Get a Patient by id")
    @Test
    public void testGetPatient() {

        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com","123456", null,null,null, null);
        Response responseCre = doCreatePatient(patient);
        Patient patientCreated = responseCre.then().assertThat().statusCode(200).extract().as(Patient.class);

        UUID patientCreatedId = patientCreated.getId();
        TestUtil.checkObjectIsNotNull("Patient ID " + patientCreated.getId(), patientCreated.getId());

        Response responseGet = doGetPatient(patientCreatedId);
        Patient patientRetrieved = responseGet.then().assertThat().statusCode(200).extract().as(Patient.class);
        Assertions.assertNotNull(patientRetrieved, "Patient not retrieved");

        if (patientRetrieved != null)
            checkPatientData(patientCreated, patientRetrieved);
    }

     @Description("Get an invalid Excel file with patients")
    @Test
    public void testGetPatientsInvalidExcel() {
    Response response = given().when().get("/patients/report");
    int status = response.then().extract().statusCode();
    TestUtil.checkField("Status Code", 500, status);
    }

    @Description("Get an Excel file with patients")
    @Test
    public void testGetPatientsExcel() {
        Response response = given().accept("*/*")
            .when().get("/patients/report");
        int status = response.then().extract().statusCode();
        TestUtil.checkANumber("Status Code",200,status);
        byte[] res = response.then().extract().asByteArray();
        String contentType = response.then().extract().contentType();
        TestUtil.checkTrue("Received file (more than 0-byte)", res.length > 0);
        TestUtil.checkTrue("Is an Excel file", contentType.contains("officedocument.spreadsheetml"));
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
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com", null,null, null,null,null);

        Response response = doCreatePatient(patient);
        Patient patientCreated = response.then().assertThat().statusCode(200)
                .extract().as(Patient.class);
        Assertions.assertNotNull(patientCreated, "Patient not created");
        UUID patientCreatedId = patientCreated.getId();
        Response responseDel = doDeletePatient(patientCreatedId);
        int statusCodeDeleted = responseDel.then().extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 200, statusCodeDeleted);

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
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com", null,null, null,null,null);
        Response response = doCreatePatient(patient);
        Patient patientCreated = response.then().assertThat().statusCode(200)
            .extract().as(Patient.class);
        Assertions.assertNotNull(patientCreated, "Patient not created");
        patientCreated.setEmail("new@emailchanged.com");
        UUID patientCreatedId = patientCreated.getId();

        Response responseUpdated = doUpdatePatient(patientCreated, patientCreatedId);
        Patient finalCheck = responseUpdated.then().assertThat().statusCode(200)
            .extract().as(Patient.class);
        Assertions.assertNotNull(responseUpdated, "Patient not updated");
        checkPatientData(patientCreated, finalCheck);
    }

    @Description("Update non-existent patient, that is Create new Patient")
    @Test
    public void testUpdateUnexistingPatient()
    {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com",null,null, null,null,null);
        Patient patientCreated = given().body(patient)
            .when().put("/patients/38400000-8cf0-11bd-b23e-10b96e4ef00d")
            .then()
            .extract().as(Patient.class);
        Assertions.assertNotNull(patientCreated, "Patient not created");

        checkPatientData(patient, patientCreated);
    }
}
