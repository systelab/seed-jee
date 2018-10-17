package com.systelab.seed;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.module.jsv.JsonSchemaValidatorSettings;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class FunctionalTest {

    protected static JsonSchemaFactory jsonSchemaFactory;
    protected static String bearer;

    private static String testUserName = "Systelab";
    private static String testPassword = "Systelab";

    @BeforeAll
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

        setupJsonValidation();
        bearer = login();
    }

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

    // To remove this configuration call the reset method 'JsonSchemaValidator.reset()'
    private static void setupJsonValidation() {
        jsonSchemaFactory = JsonSchemaFactory.newBuilder()
                .setValidationConfiguration(
                        ValidationConfiguration.newBuilder()
                                .setDefaultVersion(SchemaVersion.DRAFTV4)
                                .freeze()).freeze();

        JsonSchemaValidator.settings = JsonSchemaValidatorSettings.settings()
                .with().jsonSchemaFactory(jsonSchemaFactory)
                .and().with().checkedValidation(false);
    }

    public static String login() {
        Response response = given().contentType("application/x-www-form-urlencoded").formParam("login", testUserName).formParam("password", testPassword).
                when().post("/users/login");
        return response.getHeader("Authorization");
    }
}
