package com.systelab.seed.rest;

import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

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
    @DisplayName("Test incorrect input data checking the unexpected behaviour")
    public void given_patient_request_check_response_test() {
        Response response = doGetResponse("/patients");
        System.out.println("-> patientsResponse: " + response.print());

    }

}
