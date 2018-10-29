package com.systelab.seed.health;

import com.systelab.seed.RESTResourceTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class HealthResourceTest extends RESTResourceTest {

    @Test
    public void testHealth() {
        given().
                when().get("/health").
                then().statusCode(200);
    }
}
