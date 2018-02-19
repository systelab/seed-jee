package com.systelab.seed.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.Properties;

@DisplayName("Base Test to load all the things that the nested tests needs")
public class FunctionalTest {

    @BeforeAll
    @DisplayName("Will be executed once before all test methods in the current class")
    public static void setUp() {
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
        RestAssured.defaultParser = Parser.JSON;

        System.out.println(RestAssured.baseURI + ":" + RestAssured.port + RestAssured.basePath);
    }

    @DisplayName("Given an endpoint return the Response accordingly")
    public static Response doGetResponse(String endpoint) {
        return given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                when().get(endpoint).
                then().contentType(ContentType.JSON).extract().response();
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
