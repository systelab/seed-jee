package com.systelab.seed.rest;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.systelab.seed.client.PatientClient;
import com.systelab.seed.client.RequestException;
import com.systelab.seed.model.patient.Address;
import com.systelab.seed.model.patient.Patient;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.module.jsv.JsonSchemaValidatorSettings;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.Properties;

@DisplayName("Base Test to load all the things that the nested tests needs")
public class FunctionalTest {

    protected static JsonSchemaFactory jsonSchemaFactory;
    protected static PatientClient clientForPatient;

    @BeforeAll
    @DisplayName("Will be executed once before all test methods in the current class")
    public static void setUp() throws RequestException {
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

        createTestData();
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

    public static void createTestData() throws RequestException {
        clientForPatient = new PatientClient();

        Patient patient = new Patient();
        patient.setName("Josh");
        patient.setSurname("Long");
        patient.setEmail("josh_long@gmail.com");

        Address address = new Address();
        address.setStreet("Spring Street, 123");
        address.setCity("Worldwide");
        address.setZip("08110");
        patient.setAddress(address);
        clientForPatient.create(patient);
    }

}
