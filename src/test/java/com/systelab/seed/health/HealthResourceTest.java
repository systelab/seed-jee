package com.systelab.seed.health;

import com.systelab.seed.RESTResourceTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class HealthResourceTest extends RESTResourceTest {

    @Test
    public void testHealth() {
        given().contentType(ContentType.TEXT)
                .when().accept(ContentType.TEXT).get("/health")
                .then().assertThat().statusCode(200);
    }
}
