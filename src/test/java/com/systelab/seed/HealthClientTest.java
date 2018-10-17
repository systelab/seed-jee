package com.systelab.seed;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class HealthClientTest extends FunctionalTest {

    @Test
    public void testHealth() {
        given().
                when().get("/health").
                then().statusCode(200);
    }

}
