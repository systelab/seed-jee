package com.systelab.seed.patient.control;

import com.systelab.seed.infrastructure.SLF4JLogger;
import feign.RequestLine;
import feign.hystrix.HystrixFeign;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

interface IdentityClient {

    @RequestLine("GET /identity/v1/medical-record-number")
    public String getMedicalRecordNumber();
}

public class MedicalRecordNumberService {

    @Inject
    @SLF4JLogger
    private Logger logger;

    @Inject
    @ConfigProperty(name="medicalRecordNumberServiceUrl", defaultValue = "http://localhost:8080")
    private String medicalRecordNumberServiceUrl;

    public String getMedicalRecordNumber() {

        logger.info(String.format("medicalRecordNumberServiceUrl: %s", medicalRecordNumberServiceUrl));

        IdentityClient client = HystrixFeign.builder().target(IdentityClient.class, medicalRecordNumberServiceUrl, MedicalRecordNumberService::defaultMedicalRecordNumber);
        return client.getMedicalRecordNumber();
    }

    private static String defaultMedicalRecordNumber() {
        return "UNDEFINED";
    }

}