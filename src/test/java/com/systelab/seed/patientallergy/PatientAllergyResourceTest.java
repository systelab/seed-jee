package com.systelab.seed.patientallergy;

import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.patient.entity.Patient;
import com.systelab.seed.patientallergy.entity.PatientAllergy;
import com.systelab.seed.patientallergy.entity.PatientAllergySet;
import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Set;

import static io.restassured.RestAssured.given;

@TmsLink("TC0003_PatientAllergyManagement_IntegrationTest")
@Feature("Patient Allergy Test Suite.\n\nGoal:\nThe goal of this TC is to verify that the allergies for a patient management actions (CRUD) behave as expected according the specifications and the input values.\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class PatientAllergyResourceTest extends RESTResourceTest {

    private Allergy getAllergyData(String name, String signs) {
        Allergy allergy = new Allergy();
        allergy.setName(name);
        allergy.setSigns(signs);
        return allergy;
    }

    private Patient getPatientData(String name, String surname) {
        Patient patient = new Patient();
        patient.setName(name);
        patient.setSurname(surname);
        return patient;
    }

    private Patient createAPatient() {
        String expectedName = "Jane";
        String expectedSurname = "Senfield";

        Patient patient = getPatientData(expectedName, expectedSurname);
        Patient patientCreated = given().body(patient)
                .when().post("/patients/patient")
                .then().assertThat().statusCode(200)
                .extract().as(Patient.class);
        return patientCreated;
    }

    private Set<PatientAllergy> getPatientAllergies(Patient patient) {
        Set<PatientAllergy> allergies = given()
                .when().get("/patients/" + patient.getId() + "/allergies")
                .then().assertThat().statusCode(200)
                .extract().as(PatientAllergySet.class);
        return allergies;
    }

    private Allergy createAnAllergy() {
        String expectedAllergyName = "Some Allergy";
        String expectedAllergySigns = "Some signs";

        Allergy allergy = getAllergyData(expectedAllergyName, expectedAllergySigns);
        Allergy allergyCreated = given().body(allergy)
                .when().post("/allergies/allergy")
                .then().assertThat().statusCode(200)
                .extract().as(Allergy.class);
        return allergyCreated;
    }

    private PatientAllergy addAnAllergyToAPatient(Patient patient, PatientAllergy patientAllergy) {
        PatientAllergy patientAllergyCreated = given().body(patientAllergy)
                .when().post("/patients/" + patient.getId() + "/allergies/allergy")
                .then().assertThat().statusCode(200)
                .extract().as(PatientAllergy.class);
        return patientAllergyCreated;
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
        PatientAllergy patientAllergyCreated = addAnAllergyToAPatient(patientCreated, patientAllergy);

        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);

        TestUtil.checkANumber("Expect 1 allergy", 1, allergies.size());
    }

    @Description("Add more than one allergy to a patient")
    @Test
    public void testAddMoreThanOneAllergyToAPatient() {

        Patient patientCreated = createAPatient();
        Allergy allergyCreated1 = createAnAllergy();
        Allergy allergyCreated2 = createAnAllergy();


        PatientAllergy patientAllergy1 = new PatientAllergy(patientCreated, allergyCreated1);
        patientAllergy1.setNote("A note");
        PatientAllergy patientAllergyCreated1 = addAnAllergyToAPatient(patientCreated, patientAllergy1);

        PatientAllergy patientAllergy2 = new PatientAllergy(patientCreated, allergyCreated2);
        patientAllergy2.setNote("A note");
        PatientAllergy patientAllergyCreated2 = addAnAllergyToAPatient(patientCreated, patientAllergy2);

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
        PatientAllergy patientAllergyCreated1 = addAnAllergyToAPatient(patientCreated, patientAllergy);


        patientAllergy.setNote(noteToBeModified);
        PatientAllergy patientAllergyCreated2 = addAnAllergyToAPatient(patientCreated, patientAllergy);

        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);

        Iterator<PatientAllergy> iterator = allergies.iterator();
        PatientAllergy first = iterator.next();

        TestUtil.checkANumber("Expect 1 allergy", 1, allergies.size());
        TestUtil.checkField("Note", noteToBeModified, first.getNote());
    }

    @Description("Delete an allergy to a patient")
    @Test
    public void testDeleteAnAllergyFromAPatient() {

        Patient patientCreated = createAPatient();
        Allergy allergyCreated = createAnAllergy();

        PatientAllergy patientAllergy = new PatientAllergy(patientCreated, allergyCreated);
        patientAllergy.setNote("A note");
        PatientAllergy patientAllergyCreated = addAnAllergyToAPatient(patientCreated, patientAllergy);

        removeAnAllergyFromAPatient(patientCreated, allergyCreated);

        Set<PatientAllergy> allergies = getPatientAllergies(patientCreated);

        TestUtil.checkANumber("Expect 1 allergy", 0, allergies.size());
    }

}