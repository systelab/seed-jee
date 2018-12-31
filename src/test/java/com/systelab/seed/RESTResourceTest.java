package com.systelab.seed;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class RESTResourceTest {

    protected static final String AUTHORIZATION_HEADER = "Authorization";

    private static RequestSpecBuilder requestSpecBuilder;

    @BeforeAll
    public static void setUp() {
        RestAssured.port = getPort("server.port", 8080);
        RestAssured.basePath = getProperty("server.base", "/seed/v1/");
        RestAssured.baseURI = getProperty("server.host", "http://localhost");
        RestAssured.defaultParser = Parser.JSON;

        if (requestSpecBuilder == null) {
            String bearer = login("Systelab", "Systelab");
            requestSpecBuilder = new RequestSpecBuilder().addHeader(AUTHORIZATION_HEADER, bearer)
                    .setAccept(ContentType.JSON).setContentType(ContentType.JSON);
            RestAssured.requestSpecification = requestSpecBuilder.build();
        }

        System.out.println(RestAssured.baseURI + ":" + RestAssured.port + RestAssured.basePath);
    }

    private static String login(String username, String password) {
        return given().contentType("application/x-www-form-urlencoded").formParam("login", username).formParam("password", password).
                when().post("/users/login").getHeader(AUTHORIZATION_HEADER);
    }

    private static Integer getPort(String property, int defaultValue) {
        try {
            Properties p = new Properties();
            p.load(RESTResourceTest.class.getResourceAsStream("./client/test.properties"));
            String port = p.getProperty(property);
            if (port == null) return Integer.valueOf(defaultValue);
            else return Integer.valueOf(port);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load test.properties file in package " + RESTResourceTest.class.getPackage().getName(), e);
        }
    }

    private static String getProperty(String property, String defaultValue) {
        String readedProperty = System.getProperty(property);
        if (readedProperty == null) return defaultValue;
        else return readedProperty;
    }
}
