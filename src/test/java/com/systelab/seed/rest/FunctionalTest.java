package com.systelab.seed.rest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;

@DisplayName("Base Test to load all the things that the nested tests needs")
public class FunctionalTest {

    @Test
    @Disabled("For learning purposes")
    public void make_sure_that_google_is_up() {
        given().when().get("http://www.google.com").then().statusCode(200);
    }

    @BeforeAll
    @DisplayName("Will be executed once before all test methods in the current class")
    public static void setup() {
        String port = getPort();
        if (port == null)
            RestAssured.port = Integer.valueOf(8080);
        else
            RestAssured.port = Integer.valueOf(port);

        String basePath = System.getProperty("server.base");
        if (basePath == null)
            basePath = "/seed/v1/";
        RestAssured.basePath = basePath;

        String baseHost = System.getProperty("server.host");
        if (baseHost == null)
            baseHost = "http://localhost";
        RestAssured.baseURI = baseHost;

        System.out.println(RestAssured.baseURI + ":" + RestAssured.port + RestAssured.basePath);
    }

    private static String getPort() {
        try {
            Properties p = new Properties();
            p.load(FunctionalTest.class.getResourceAsStream("../client/test.properties"));
            return p.getProperty("server.port");
        } catch (IOException e) {
            throw new IllegalStateException("Could not load test.properties file in package " + FunctionalTest.class.getPackage().getName(), e);
        }
    }

}
