package com.systelab.seed.unit;

import com.systelab.seed.client.PatientClient;
import com.systelab.seed.client.RequestException;
import com.systelab.seed.model.patient.Address;
import com.systelab.seed.model.patient.Patient;

import java.util.List;
import java.util.logging.Logger;

//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.qatools.allure.annotations.Description;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Severity;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.allure.annotations.TestCaseId;
import ru.yandex.qatools.allure.annotations.Title;
import ru.yandex.qatools.allure.model.DescriptionType;
import ru.yandex.qatools.allure.model.SeverityLevel;

@Title("Patients Test Suite")
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

    @TestCaseId("SEED-SCC-1")
    @Description(value = "Test that is possible to create a Patient.\n\nPrerequisites:\n\n" + "- Prerequisite 1\n" + "- Prerequisite 2\n" + "- Prerequisite 3\n", type = DescriptionType.MARKDOWN)
    @Features("Patient")
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

    @TestCaseId("SEED-SCC-2")
    @Description("Test that it is not possible to create a Invalid Patient and that we have an exception.")
    @Features("Patient")
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

    @TestCaseId("SEED-SCC-3")
    @Description("Test that we can get a List of Patients.")
    @Features("Patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetPatientList() throws RequestException {
        List<Patient> patients = clientForPatient.get();
        for (int i = 0; i < patients.size(); i++) {
            logger.info(patients.get(i).getName());
        }
        Assertions.assertNotNull(patients);
    }

    @TestCaseId("SEED-SCC-4")
    @Description("Test that we can get a Patient.")
    @Features("Patient")
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

    @TestCaseId("SEED-SCC-5")
    @Description("Test that we can delete a Patient.")
    @Features("Patient")
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