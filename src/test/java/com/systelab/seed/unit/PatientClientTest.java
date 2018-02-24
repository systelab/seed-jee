package com.systelab.seed.unit;

import com.systelab.seed.client.PatientClient;
import com.systelab.seed.client.RequestException;
import com.systelab.seed.model.patient.Address;
import com.systelab.seed.model.patient.Patient;

import java.util.List;
import java.util.logging.Logger;

import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;



@Feature("Patients Test Suite")
public class PatientClientTest extends BaseClientTest {
    private static final Logger logger = Logger.getLogger(PatientClientTest.class.getName());

    public static PatientClient clientForPatient;

    @BeforeAll
    public static void init() throws RequestException {
        clientForPatient = new PatientClient();
        login(clientForPatient);
    }

    @Step("Create the patient {0}")
    public Patient createPatient(Patient patient) throws RequestException {
        Patient patient2 = clientForPatient.create(patient);
        return patient2;
    }

    @Step("Delete the patient {0}")
    public boolean deletePatient(Patient patient) throws RequestException {
        return clientForPatient.delete(patient.getId());
    }

    @Step("Check that the returning value {0} is true")
    public void checkResultIsTrue(boolean b) {
        Assertions.assertTrue(b);
    }

    @TmsLink("SEED-SCC-1")
    @Issue("ISSUE-1")
    @Link(value = "REQ-PAT-1", type="requirement")
    @DisplayName("Test create a Patient.")
    @Description("Test that is possible to create a Patient.\n\nPrerequisites:\n\n" + "- Prerequisite 1\n" + "- Prerequisite 2\n" + "- Prerequisite 3\n")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testCreatePatient() throws RequestException {
        Patient patient = new Patient();
        patient.setName("Ralph");
        patient.setSurname("Burrows");
        patient.setEmail("rburrows@gmail.com");

        Address address = new Address();
        address.setStreet("E-Street, 90");
        address.setCity("Barcelona");
        address.setZip("08021");
        patient.setAddress(address);
        Patient patient2 = createPatient(patient);

        Assertions.assertNotNull(patient2);
    }

    @TmsLink("SEED-SCC-2")
    @DisplayName("Test create invalid Patient.")
    @Description("Test that it is not possible to create a Invalid Patient and that we have an exception.")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testCreateInvalidPatient() {
        Patient patient = new Patient();
        patient.setName("William");
        patient.setSurname("Burrows");

        patient.setEmail("rburrows");

        Address address = new Address();
        address.setStreet("E-Street, 90");
        address.setCity("Barcelona");
        address.setZip("08021");
        patient.setAddress(address);
        Exception caughtException = null;
        try {
            createPatient(patient);
        } catch (Exception ex) {
            caughtException = ex;
        }

        Assertions.assertEquals(400, ((RequestException) caughtException).getErrorCode(), "Invalid error code exception" );
    }

    @TmsLink("SEED-SCC-3")
    @DisplayName("Test Patient List.")
    @Description("Test that we can get a List of Patients.")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetPatientList() throws RequestException {
        List<Patient> patients = clientForPatient.get();
        for (int i = 0; i < patients.size(); i++) {
            logger.info(patients.get(i).getName());
        }
        Assertions.assertNotNull(patients);
    }

    @TmsLink("SEED-SCC-4")
    @DisplayName("Test get a Patient.")
    @Description("Test that it is possible to get a patient.")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetPatient() throws RequestException {

        List<Patient> patients = clientForPatient.get();
        for (int i = 0; i < patients.size(); i++) {
            System.out.println("Looking for " + patients.get(i).getId());
            Patient patient = clientForPatient.get(patients.get(i).getId());
            //   get("seed/v1/patients/"+patients.get(i).getId()).then().body("name", Matchers.equalTo("Ralph"));
            Assertions.assertNotNull(patient);
        }
    }

    @TmsLink("SEED-SCC-5")
    @DisplayName("Test delete a Patient.")
    @Description("Test that we can delete a Patient.")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testDeletePatient() throws RequestException {
        Patient patient = new Patient();
        patient.setName("Ralph");
        patient.setSurname("Burrows");
        patient.setEmail("rburrows@gmail.com");
        Address address = new Address();
        address.setStreet("E-Street, 90");
        address.setCity("Barcelona");
        address.setZip("08021");
        patient.setAddress(address);
        Patient patient2 = createPatient(patient);
        boolean result = deletePatient(patient2);
        checkResultIsTrue(result);
    }
}