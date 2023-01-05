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
import java.util.UUID;

import static io.restassured.RestAssured.given;
import io.restassured.response.ValidatableResponse;
import static java.util.stream.Collectors.joining;


@TmsLink("TC0002_AllergyManagement_IntegrationTest")
@Feature("Allergy Test Suite.\n\nGoal:\nThe goal of this TC is to verify that the Allergies management actions (CRUD) behave as expected according the specifications and the input values.\n\nEnvironment:\n...\nPreconditions:\nN/A.")
class AllergyResourceTest extends RESTResourceTest {

    private Response doCreateAllergy(Allergy allergy){
        return given().body(allergy).when().post("/allergies/allergy");
    }

    private Response doGetAllergy(UUID id){
        return given().when().get("/allergies/" + id);
    }

    private Response doGetAllergyList(){
        return given().when().get("/allergies/");
    }

    private Response doDeleteAllergy(UUID id){
        return given().when().delete("/allergies/" + id);
    }

    private Response doUpdateAllergy(Allergy allergyCreated, UUID id){
        return given().body(allergyCreated)
            .when().put("/allergies/" + id);
    }

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
        if (expected.getSymptoms() != null)
            TestUtil.checkField("Symptoms", expected.getSymptoms(), actual.getSymptoms());
    }

    @Description("Create a new allergy with name, sign and symptoms")
    @Test
    void testCreateAllergy() {
        String expectedName = "Tree pollen";
        String expectedSigns = "Watering eyes";

        Allergy allergy = getAllergyData(expectedName, expectedSigns, null);
        Response response = doCreateAllergy(allergy);
        Allergy allergyCreated = response.then().extract().as(Allergy.class);
        int status = response.then().extract().statusCode();
        TestUtil.checkField("Status Code", 200, status);
        checkAllergyData(allergy, allergyCreated);
    }

    @Description("Create a new allergy with all information")
    @Test
    void testCreateAllergyAllInfo() {
        String expectedName = "Tree pollen";
        String expectedSigns = "Watering eyes";
        String expectedSymptoms = "Sneezing";
        Allergy allergy = getAllergyData(expectedName, expectedSigns, expectedSymptoms);
        Response response = doCreateAllergy(allergy);
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
    void testCreateInvalidAllergyMandatoryFieldsEmpty() {
        testCreateInvalidAllergy(getAllergyData("", "", "Sneezing"));
        testCreateInvalidAllergy(getAllergyData("", "Sneezing", "Sneezing"));
        testCreateInvalidAllergy(getAllergyData("Pollen", "", "Sneezing"));
    }

    @Description("Create a Allergy with invalid data: name, signs or symptoms field too long")
    @Test
    void testCreateInvalidAllergyTooLongText() {
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
            Response response = doCreateAllergy(allergy);
            Allergy allergyCreated = response.then().assertThat().statusCode(200)
                    .extract().as(Allergy.class);

            Assertions.assertNotNull(allergyCreated.getId(), "Allergy not created");
        }
    }

    @Description("Get a list of allergies")
    @Test
    void testGetAllergyList() {
        int numberOfAllergies = 5;

        Response responseBefore = doGetAllergyList();
        AllergiesPage allergiesBefore= responseBefore.then().assertThat().statusCode(200)
            .extract().as(AllergiesPage.class);
        long initialSize = allergiesBefore.getTotalElements();
        saveAllergiesDatabase(allergiesBefore.getContent());

        createSomeAllergies(numberOfAllergies);

        Response responseAfter = doGetAllergyList();
        AllergiesPage allergiesAfter= responseAfter.then().assertThat().statusCode(200)
            .extract().as(AllergiesPage.class);
        long finalSize = allergiesAfter.getTotalElements();
        saveAllergiesDatabase(allergiesAfter.getContent());

        TestUtil.checkANumber("List size", initialSize + numberOfAllergies, finalSize);
    }

    @Description("Get a Allergy by id")
    @Test
    void testGetAllergy() {
        Allergy allergy = getAllergyData("Tree pollen", "Watering eyes", "Dry, red and cracked skin");
        Response responseCre = doCreateAllergy(allergy);
        Allergy allergyCreated = responseCre.then().assertThat().statusCode(200)
            .extract().as(Allergy.class);

        UUID allergyCreatedId = allergyCreated.getId();
        TestUtil.checkObjectIsNotNull("Allergy ID " + allergyCreatedId, allergyCreatedId);

        Response responseGet = doGetAllergy(allergyCreatedId);
        Allergy allergyRetrieved = responseGet.then().assertThat().statusCode(200)
                .extract().as(Allergy.class);
        Assertions.assertNotNull(allergyRetrieved, "Allergy not retrieved");
        checkAllergyData(allergy, allergyRetrieved);
    }

    @Description("Get a allergy with an non-existing id")
    @Test
    void testGetUnexistingAllergy() {
        int statusCode = given()
                .when().get("/allergies/38400000-8cf0-11bd-b23e-10b96e4ef00d")
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 404, statusCode);
    }

    @Description("Delete a allergy by id")
    @Test
    void testDeleteAllergy() {
        Allergy allergy = getAllergyData("Tree pollen", "Watering eyes", "Dry, red and cracked skin");
        Response responseCre = doCreateAllergy(allergy);
        Allergy allergyCreated = responseCre.then().assertThat().statusCode(200)
            .extract().as(Allergy.class);

        Assertions.assertNotNull(allergyCreated, "Allergy not created");
        UUID allergyCreatedId = allergyCreated.getId();
        Response responseDel = doDeleteAllergy(allergyCreatedId);
        int statusCodeDeleted = responseDel.then().extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 200, statusCodeDeleted);

        int statusCode = given()
                .when().get("/allergies/" + allergyCreated.getId())
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 404, statusCode);
    }

    @Description("Delete non-existing Allergy")
    @Test
    void testDeleteUnexistingAllergy() {
        int statusCode = given()
                .when().delete("/allergies/38400000-8cf0-11bd-b23e-10b96e4ef00d")
                .then()
                .extract().statusCode();
        TestUtil.checkField("Status Code", 404, statusCode);
    }

    @Description("Update a allergy by id")
    @Test
    void testUpdateAllergy() {
        Allergy allergy = getAllergyData("Tree pollen", "Watering eyes", "Dry, red and cracked skin");
        Response responseCre = doCreateAllergy(allergy);
        Allergy allergyCreated = responseCre.then().assertThat().statusCode(200)
            .extract().as(Allergy.class);
        Assertions.assertNotNull(allergyCreated, "Allergy not created");

        allergyCreated.setSymptoms("Sneezing");

        UUID allergyCreatedId = allergyCreated.getId();
        Response responseUpd = doUpdateAllergy(allergyCreated, allergyCreatedId);
        Allergy allergyUpdated = responseUpd.then().assertThat().statusCode(200)
            .extract().as(Allergy.class);
        Assertions.assertNotNull(allergyUpdated, "Allergy not updated");
        checkAllergyData(allergyCreated, allergyUpdated);
    }

    @Description("Update non-existent allergy, that is Create new Allergy")
    @Test
    void testUpdateUnexistingAllergy() {
        Allergy allergy = getAllergyData("Tree pollen", "Watering eyes", "Dry, red and cracked skin");
        Allergy allergyCreated = given().body(allergy)
                .when().put("/allergies/38400000-8cf0-11bd-b23e-10b96e4ef00d")
                .then()
                .extract().as(Allergy.class);
        Assertions.assertNotNull(allergyCreated, "Allergy not created");

        TestUtil.checkField("Name", allergyCreated.getName(), allergy.getName());
    }
}
