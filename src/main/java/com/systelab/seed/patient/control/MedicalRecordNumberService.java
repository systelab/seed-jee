package com.systelab.seed.patient.control;

import feign.RequestLine;
import feign.hystrix.HystrixFeign;


import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

interface IdentityClient {

    @RequestLine("GET /identity/v1/medical-record-number")
    String getMedicalRecordNumber();
}

public class MedicalRecordNumberService {

    private  static final String UNDEFINED = "UNDEFINED";

    @Inject
    private Logger logger;

    @Inject
    @ConfigProperty(name="medicalRecordNumberServiceUrl", defaultValue = "http://localhost:8080")
    private String medicalRecordNumberServiceUrl;

    public String getMedicalRecordNumber() {

        if (logger.isInfoEnabled()){
            logger.info(String.format("medicalRecordNumberServiceUrl: %s", medicalRecordNumberServiceUrl));
        }
        IdentityClient client = HystrixFeign.builder().target(IdentityClient.class, medicalRecordNumberServiceUrl, MedicalRecordNumberService::defaultMedicalRecordNumber);
        return client.getMedicalRecordNumber();
    }

    private static String defaultMedicalRecordNumber() {
        return UNDEFINED;
    }

}