package com.systelab.seed;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class HealthResourceTest extends FunctionalTest {

    @Test
    public void testHealth() {
        given().
                when().get("/health").
                then().statusCode(200);
    }

}
