package com.systelab.seed.rest;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Test Class to check Seed REST Api behaviour following a BDD approach (given-when-then)
 *
 * Note: See FunctionalTest parent class to understand how to obtain the rest entry endpoint
 * Note: Consider to change the method naming to pass Codacy checks
 */
public class SeedRestTest extends FunctionalTest {

    @Test
    @Disabled("With this @Disabled Annotation we can skip the test putting the reason as a comment")
    public void make_sure_that_google_is_up() {
        given().when().get("http://www.google.com").then().statusCode(200);
    }

    @Test
    @DisplayName("Basic ping test to check that seed REST API is up")
    public void basic_ping_test() {
        given().when().get("/patients").then().statusCode(200);
    }

    @Test
    @DisplayName("Test incorrect input data checking the unexpected behaviour")
    public void invalid_patient_id_test() {
        given().when().get("/patients/patient/155").then().statusCode(404);
    }

    @Test
    @DisplayName("Test to check the expected body response content")
    public void given_patient_request_check_body_response() {
        // given-when calling doGetResponse method
        Response response = doGetResponse("/patients");
        response.then().body(containsString("Worldwide"));
    }

    @Test
    @DisplayName("Test to check Json content in an attribute")
    public void given_Url_whenSuccessOnGetsResponseAndJsonHasRequiredKV_thenCorrect() {
        given().when().get("patients").then().statusCode(200).assertThat()
                .body("address.zip", hasItems("08110"));
    }

    @DisplayName("Test Json Schema Response")
    @Disabled()
    @Test
    public void givenUrl_whenValidatesResponseWithStaticSettings_thenCorrect() {
        Response response = doGetResponse("/patients");
        response.then().assertThat().body(matchesJsonSchemaInClasspath
                ("json-schema.json").using(JsonSchemaValidator.settings.with().checkedValidation(false)));
    }

}
