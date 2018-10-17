package com.systelab.seed.unit;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


@TmsLink("TC0003_HealthManagement_IntegrationTest")
@Feature("Check that the service is healthy by calling a REST endpoint.")
@DisplayName("Health Check Test Suite")
public class HealthClientTest extends FunctionalTest {

    @DisplayName("Get the Health status")
    @Description("Action: Get the Health status and check that is OK.")
    @Tag("health")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testHealth() {
        given().
                when().
                get("/health").then().statusCode(200);
    }

}
