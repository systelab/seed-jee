package com.systelab.seed.patientallergy;

import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.patient.entity.Address;
import com.systelab.seed.patient.entity.Patient;
import com.systelab.seed.patientallergy.entity.PatientAllergy;
import com.systelab.seed.patientallergy.entity.PatientAllergySet;
import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

@TmsLink("TC0003_PatientAllergyManagement_IntegrationTest")
@Feature("Patient Allergy Test Suite.\n\nGoal:\nThe goal of this TC is to verify that the allergies for a patient management actions (CRUD) behave as expected according the specifications and the input values.\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class PatientAllergyResourceTest extends RESTResourceTest {

    private Response doCreatePatient(Patient patient){ return given().body(patient).when().post("/patients/patient"); }

    private Allergy getAllergyData(String name, String signs, String symptoms) {
        Allergy allergy = new Allergy();
        allergy.setName(name);
        allergy.setSigns(signs);
        if (symptoms != null)
            allergy.setSymptoms(symptoms); //can be NULL
        return allergy;
    }

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

    private Patient createAPatient() {
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
        return given().body(patient)
                .when().post("/patients/patient")
                .then().assertThat().statusCode(200)
                .extract().as(Patient.class);
    }

    private Set<PatientAllergy> getPatientAllergies(Patient patient) {
        return given()
                .when().get("/patients/" + patient.getId() + "/allergies")
                .then().assertThat().statusCode(200)
                .extract().as(PatientAllergySet.class);
    }

    private Allergy createAnAllergy() {
        String expectedAllergyName = "Some Allergy";
        String expectedAllergySigns = "Some signs";

        Allergy allergy = getAllergyData(expectedAllergyName, expectedAllergySigns, null);
        return given().body(allergy)
                .when().post("/allergies/allergy")
                .then().assertThat().statusCode(200)
                .extract().as(Allergy.class);
    }

    private PatientAllergy addAnAllergyToAPatient(Patient patient, PatientAllergy patientAllergy) {
        return given().body(patientAllergy)
                .when().post("/patients/" + patient.getId() + "/allergies/allergy")
                .then().assertThat().statusCode(200)
                .extract().as(PatientAllergy.class);
    }

    private PatientAllergy updateAnAllergyToAPatient(Patient patient, PatientAllergy patientAllergy) {
        return given().body(patientAllergy)
                .when().put("/patients/" + patient.getId() + "/allergies/" + patientAllergy.getAllergy().getId())
                .then().assertThat().statusCode(200)
                .extract().as(PatientAllergy.class);
    }

    private void removeAnAllergyFromAPatient(Patient patient, Allergy allergy) {
        given()
                .when().delete("/patients/" + patient.getId() + "/allergies/" + allergy.getId())
                .then().assertThat().statusCode(200);
    }

    @Description("Get the allergies of a new patient")
    @Test
    public void testGetTheAllergiesOfANewPatient() {

        Patient patientCreated = createAPatient();
        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);
        TestUtil.checkANumber("Expect non allergies", 0, allergies.size());
    }

    @Description("Add an allergy to a patient")
    @Test
    public void testAddAnAllergyToAPatient() {

        Patient patientCreated = createAPatient();
        Allergy allergyCreated = createAnAllergy();

        PatientAllergy patientAllergy = new PatientAllergy(patientCreated, allergyCreated);
        patientAllergy.setNote("A note");
        addAnAllergyToAPatient(patientCreated, patientAllergy);

        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);

        TestUtil.checkANumber("Expect 1 allergy", 1, allergies.size());
    }

    @Description("Add an allergy to a patient without notes")
    @Test
    public void testAddAnAllergyWithoutNotesToAPatient() {

        Patient patientCreated = createAPatient();
        Allergy allergyCreated = createAnAllergy();

        PatientAllergy patientAllergy = new PatientAllergy(patientCreated, allergyCreated);

        int code = given().body(patientAllergy)
                .when().post("/patients/" + patientCreated.getId() + "/allergies/allergy")
                .then().assertThat()
                .extract().statusCode();

        TestUtil.checkANumber("Expect an error 400", 400, code);
    }

    @Description("Add more than one allergy to a patient")
    @Test
    public void testAddMoreThanOneAllergyToAPatient() {

        Patient patientCreated = createAPatient();
        Allergy allergyCreated1 = createAnAllergy();
        Allergy allergyCreated2 = createAnAllergy();


        PatientAllergy patientAllergy1 = new PatientAllergy(patientCreated, allergyCreated1);
        patientAllergy1.setNote("A note");
        addAnAllergyToAPatient(patientCreated, patientAllergy1);

        PatientAllergy patientAllergy2 = new PatientAllergy(patientCreated, allergyCreated2);
        patientAllergy2.setNote("A note");
        addAnAllergyToAPatient(patientCreated, patientAllergy2);

        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);

        TestUtil.checkANumber("Expect 2 allergy", 2, allergies.size());
    }

    @Description("Add the same allergy twice")
    @Test
    public void testAddAnAllergyTwiceToAPatient() {
        String noteToBeModified = "A second note";
        Patient patientCreated = createAPatient();
        Allergy allergyCreated = createAnAllergy();

        PatientAllergy patientAllergy = new PatientAllergy(patientCreated, allergyCreated);
        patientAllergy.setNote("A note");
        addAnAllergyToAPatient(patientCreated, patientAllergy);


        patientAllergy.setNote(noteToBeModified);

        int code = given().body(patientAllergy)
                .when().post("/patients/" + patientCreated.getId() + "/allergies/allergy")
                .then()
                .extract().statusCode();
        TestUtil.checkANumber("Expect an error 404", 404, code);
    }

    @Description("Update an allergy from a patient")
    @Test
    public void testUpdateAnAllergyToAPatient() {
        String noteToBeModified = "A second note";
        Patient patientCreated = createAPatient();
        Allergy allergyCreated = createAnAllergy();

        PatientAllergy patientAllergy = new PatientAllergy(patientCreated, allergyCreated);
        patientAllergy.setNote("A note");
        addAnAllergyToAPatient(patientCreated, patientAllergy);

        patientAllergy.setNote(noteToBeModified);
        updateAnAllergyToAPatient(patientCreated, patientAllergy);

        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);

        Iterator<PatientAllergy> iterator = allergies.iterator();
        PatientAllergy first = iterator.next();

        TestUtil.checkANumber("Expect 1 allergy", 1, allergies.size());
        TestUtil.checkField("Note", noteToBeModified, first.getNote());
    }

    @Description("Update an allergy to a new patient (Update is idempotent)")
    @Test
    public void testAddAnAllergyToANewPatient() {

        Patient patientCreated = createAPatient();
        Allergy allergyCreated = createAnAllergy();

        PatientAllergy patientAllergy = new PatientAllergy(patientCreated, allergyCreated);
        patientAllergy.setNote("Some notes");

        given().body(patientAllergy)
                .when().put("/patients/" + patientCreated.getId() + "/allergies/"+allergyCreated.getId())
                .then().assertThat().statusCode(200);

        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);

        TestUtil.checkANumber("Expect 1 allergy", 1, allergies.size());
    }

    @Description("Delete an allergy from a patient")
    @Test
    public void testDeleteAnAllergyFromAPatient() {

        Patient patientCreated = createAPatient();
        Allergy allergyCreated = createAnAllergy();

        PatientAllergy patientAllergy = new PatientAllergy(patientCreated, allergyCreated);
        patientAllergy.setNote("A note");
        addAnAllergyToAPatient(patientCreated, patientAllergy);

        removeAnAllergyFromAPatient(patientCreated, allergyCreated);

        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);

        TestUtil.checkANumber("Expect 1 allergy", 0, allergies.size());
    }

    @Description("Delete an unexisting allergy from a patient")
    @Test
    public void testDeleteAnUnexistingAllergyFromAPatient() {

        Patient patientCreated = createAPatient();
        Allergy allergyCreated = createAnAllergy();

        removeAnAllergyFromAPatient(patientCreated, allergyCreated);

        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);

        TestUtil.checkANumber("Expect 0 allergy", 0, allergies.size());
    }
}
