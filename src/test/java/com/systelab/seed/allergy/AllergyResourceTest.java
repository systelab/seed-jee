package com.systelab.seed.allergy;

import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.allergy.entity.AllergiesPage;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.utils.FakeNameGenerator;
import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.joining;


@TmsLink("TC0002_AllergyManagement_IntegrationTest")
@Feature("Allergy Test Suite.\n\nGoal:\nThe goal of this TC is to verify that the Allergies management actions (CRUD) behave as expected according the specifications and the input values.\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class AllergyResourceTest extends RESTResourceTest {

    private Allergy getAllergyData(String name, String signs, String symptoms) {
        Allergy allergy = new Allergy();
        allergy.setName(name);
        allergy.setSigns(signs);
        if (symptoms != null)
            allergy.setSymptoms(symptoms); //can be NULL
        return allergy;
    }

    private void checkAllergyData(Allergy expected, Allergy actual){
        TestUtil.checkObjectIsNotNull("Allergy", actual);
        TestUtil.checkField("Name", expected.getName(), actual.getName());
        TestUtil.checkField("Sign", expected.getSigns(), actual.getSigns());
        if (expected.getName() != null)
            TestUtil.checkField("Symptoms", expected.getSymptoms(), actual.getSymptoms());
    }

    @Description("Create a new allergy with name, sign and symptoms")
    @Test
    public void testCreateAllergy() {
        String expectedName = "Tree pollen";
        String expectedSigns = "Watering eyes";

        Allergy allergy = getAllergyData(expectedName, expectedSigns, null);
        Response response = given().body(allergy)
            .when().post("/allergies/allergy");
        Allergy allergyCreated = response.then().extract().as(Allergy.class);
        int status = response.then().extract().statusCode();
        TestUtil.checkField("Status Code", 200, status);
        checkAllergyData(allergy, allergyCreated);
    }


    @Description("Create a new allergy with all information")
    @Test
    public void testCreateAllergyAllInfo() {
        String expectedName = "Tree pollen";
        String expectedSigns = "Watering eyes";
        String expectedSymptoms = "Sneezing";
        Allergy allergy = getAllergyData(expectedName, expectedSigns, expectedSymptoms);
        Response response = given().body(allergy)
            .when().post("/allergies/allergy");
        Allergy allergyCreated = response.then().extract().as(Allergy.class);
        int status = response.then().extract().statusCode();
        TestUtil.checkField("Status Code", 200, status);
        checkAllergyData(allergy, allergyCreated);
    }

    private void testCreateInvalidAllergy(Allergy allergy) {

        int statusCode = given().body(allergy)
                .when().post("/allergies/allergy")
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code", 400, statusCode);
    }

    @Description("Create a Allergy with invalid data: mandatory fields empty (name, signs)")
    @Test
    public void testCreateInvalidAllergyMandatoryFieldsEmpty() {
        testCreateInvalidAllergy(getAllergyData("", "", "Sneezing"));
        testCreateInvalidAllergy(getAllergyData("", "Sneezing", "Sneezing"));
        testCreateInvalidAllergy(getAllergyData("Pollen", "", "Sneezing"));
    }

    @Description("Create a Allergy with invalid data: name, signs or symptoms field too long")
    @Test
    public void testCreateInvalidAllergyTooLongText() {
        String tooLongString = "thisStringIsIntendedToCauseAnExceptionBecauseOfItsExcessiveLengthTheMostLongStringAllowedMustHaveLessThanTeoHundredAndFiftyFiveCharactersThisShouldBeVerifiedInEveryTextFieldToEnsureTheLimitationIsWorkingProperlyThisStringOnlyHasEnglishLettersButMoreScenarios";
        testCreateInvalidAllergy(getAllergyData(tooLongString, "Watering eyes", "Sneezing"));
        testCreateInvalidAllergy(getAllergyData("Tree pollen", tooLongString, "Sneezing"));
        testCreateInvalidAllergy(getAllergyData("Tree pollen", "Watering eyes", tooLongString));
        // use it in email field too?
    }

    @Attachment(value = "Allergies Database")
    private String saveAllergiesDatabase(List<Allergy> allergies) {
        return allergies.stream().map((allergy) -> allergy.getName() + "\t" + allergy.getSigns() + "\t" + allergy.getSymptoms()).collect(joining("\n"));
    }

    @Step("Action: Create {0} allergies")
    private void createSomeAllergies(int numberOfAllergies) {
        FakeNameGenerator aFakeNameGenerator = new FakeNameGenerator();
        for (int i = 0; i < numberOfAllergies; i++) {
            Allergy allergy = getAllergyData(aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(true));
            Allergy allergyCreated = given().body(allergy)
                    .when().post("/allergies/allergy")
                    .then().assertThat().statusCode(200)
                    .extract().as(Allergy.class);

            Assertions.assertNotNull(allergyCreated.getId(), "Allergy not created");
        }
    }

    @Description("Get a list of allergies")
    @Test
    public void testGetAllergyList() {
        int numberOfAllergies = 5;

        AllergiesPage allergiesBefore = given()
                .when().get("/allergies")
                .then().assertThat().statusCode(200)
                .extract().as(AllergiesPage.class);
        long initialSize = allergiesBefore.getTotalElements();
        saveAllergiesDatabase(allergiesBefore.getContent());

        createSomeAllergies(numberOfAllergies);

        AllergiesPage allergiesAfter = given()
                .when().get("/allergies")
                .then().assertThat().statusCode(200)
                .extract().as(AllergiesPage.class);
        long finalSize = allergiesAfter.getTotalElements();
        saveAllergiesDatabase(allergiesAfter.getContent());

        TestUtil.checkANumber("List size", initialSize + numberOfAllergies, finalSize);
    }

    @Description("Get a Allergy by id")
    @Test
    public void testGetAllergy() {
        Allergy allergy = getAllergyData("Tree pollen", "Watering eyes", "Dry, red and cracked skin");
        Allergy allergyCreated = given().body(allergy)
                .when().post("/allergies/allergy")
                .then().assertThat().statusCode(200)
                .extract().as(Allergy.class);
        TestUtil.checkObjectIsNotNull("Allergy ID " + allergyCreated.getId(), allergyCreated.getId());
        Allergy allergyRetrieved = given()
                .when().get("/allergies/" + allergyCreated.getId())
                .then().assertThat().statusCode(200)
                .extract().as(Allergy.class);
        Assertions.assertNotNull(allergyRetrieved, "Allergy not retrieved");
        checkAllergyData(allergy, allergyRetrieved);
    }

    @Description("Get a allergy with an non-existing id")
    @Test
    public void testGetUnexistingAllergy() {
        int statusCode = given()
                .when().get("/allergies/38400000-8cf0-11bd-b23e-10b96e4ef00d")
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 404, statusCode);
    }

    @Description("Delete a allergy by id")
    @Test
    public void testDeleteAllergy() {
        Allergy allergy = getAllergyData("Tree pollen", "Watering eyes", "Dry, red and cracked skin");
        Allergy allergyCreated = given().body(allergy)
                .when().post("/allergies/allergy")
                .then().assertThat().statusCode(200)
                .extract().as(Allergy.class);

        Assertions.assertNotNull(allergyCreated, "Allergy not created");
        given()
                .when().delete("/allergies/" + allergyCreated.getId())
                .then().assertThat().statusCode(200);

        int statusCode = given()
                .when().get("/allergies/" + allergyCreated.getId())
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 404, statusCode);
    }

    @Description("Delete non-existing Allergy")
    @Test
    public void testDeleteUnexistingAllergy() {
        int statusCode = given()
                .when().delete("/allergies/38400000-8cf0-11bd-b23e-10b96e4ef00d")
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code", 404, statusCode);
    }

    @Description("Update a allergy by id")
    @Test
    public void testUpdateAllergy() {
        Allergy allergy = getAllergyData("Tree pollen", "Watering eyes", "Dry, red and cracked skin");
        Allergy allergyCreated = given().body(allergy)
                .when().post("/allergies/allergy")
                .then().assertThat().statusCode(200)
                .extract().as(Allergy.class);
        Assertions.assertNotNull(allergyCreated, "Allergy not created");
        allergyCreated.setSymptoms("Sneezing");
        Allergy allergyUpdated = given().body(allergyCreated)
                .when().put("/allergies/" + allergyCreated.getId())
                .then().assertThat().statusCode(200)
                .extract().as(Allergy.class);
        Assertions.assertNotNull(allergyUpdated, "Allergy not updated");
    }

    @Description("Update non-existent allergy, that is Create new Allergy")
    @Test
    public void testUpdateUnexistingAllergy() {
        Allergy allergy = getAllergyData("Tree pollen", "Watering eyes", "Dry, red and cracked skin");
        Allergy allergyCreated = given().body(allergy)
                .when().put("/allergies/38400000-8cf0-11bd-b23e-10b96e4ef00d")
                .then()
                .extract().as(Allergy.class);
        Assertions.assertNotNull(allergyCreated, "Allergy not created");

        TestUtil.checkField("Name", allergyCreated.getName(), allergy.getName());
    }
}
