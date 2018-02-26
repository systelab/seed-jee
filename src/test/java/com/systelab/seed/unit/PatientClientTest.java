package com.systelab.seed.unit;

import com.systelab.seed.FakeNameGenerator;
import com.systelab.seed.TestUtil;
import com.systelab.seed.client.PatientClient;
import com.systelab.seed.client.RequestException;
import com.systelab.seed.model.patient.Address;
import com.systelab.seed.model.patient.Patient;

import java.util.List;
import java.util.logging.Logger;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;


@Feature("Patients Test Suite")
@DisplayName("Patients Test Suite")
public class PatientClientTest extends BaseClientTest {
    private static final Logger logger = Logger.getLogger(PatientClientTest.class.getName());

    public static PatientClient clientForPatient;

    @BeforeAll
    public static void init() throws RequestException {
        clientForPatient = new PatientClient();
        login(clientForPatient);
    }


    private Patient getPatientData() {
        Patient patient = new Patient();
        patient.setName("Ralph");
        patient.setSurname("Burrows");
        patient.setEmail("rburrows@gmail.com");

        Address address = new Address();
        address.setStreet("E-Street, 90");
        address.setCity("Barcelona");
        address.setZip("08021");
        patient.setAddress(address);
        return patient;
    }

    @Step("Get a local patient object with name '{0}', surname '{1}' and email '{2}'")
    public Patient getPatientData(String name, String surname, String email) {
        Patient patient = getPatientData();
        patient.setName(name);
        patient.setSurname(surname);
        patient.setEmail(email);
        return patient;
    }

    @TmsLink("SEED-SCC-1")
    @Issue("ISSUE-1")
    @Link(value = "REQ-PAT-1", type = "requirement")
    @DisplayName("Test create a Patient.")
    @Description("Test that is possible to create a patient.\n\nPrerequisites:\n\n" + "- Prerequisite 1\n" + "- Prerequisite 2\n" + "- Prerequisite 3\n")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testCreatePatient() throws RequestException {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = clientForPatient.create(patient);
        TestUtil.checkObjectIsNotNull("patient", patientCreated);
        TestUtil.checkField("Name", "John", patientCreated.getName());
        TestUtil.checkField("Surname", "Burrows", patientCreated.getSurname());
        TestUtil.checkField("Email", "jburrows@werfen.com", patientCreated.getEmail());
    }

    @Step("Check that we have an exception if we create an invalid patient")
    public void testCreateInvalidPatient(Patient patient) {
        Exception caughtException = null;
        try {
            clientForPatient.create(patient);
        } catch (Exception ex) {
            caughtException = ex;
        }
        TestUtil.checkObjectIsNotNull("Exception", caughtException);
        TestUtil.checkThatIHaveAnExceptionException(400, ((RequestException) caughtException).getErrorCode());
    }

    @TmsLink("SEED-SCC-2")
    @Link(value = "REQ-PAT-2", type = "requirement")
    @DisplayName("Test create invalid Patient.")
    @Description("Test that it is not possible to create a patient with invalid data and that we get an exception.")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testCreateInvalidPatient() {
        testCreateInvalidPatient(getPatientData("", "Burrows", "jburrows@test.com"));
        testCreateInvalidPatient(getPatientData("John", "", "jburrows@test.com"));
        testCreateInvalidPatient(getPatientData("", "", "jburrows@test.com"));
        testCreateInvalidPatient(getPatientData("John", "Burrows", "jburrows"));
    }


    @Attachment(value = "Patients Database")
    public String savePatientsDatabase(List<Patient> patients) {
        String patientAsText = "";

        for (Patient patient : patients) {
            patientAsText += patient.getSurname() + ", " + patient.getName() + "\t" + patient.getEmail() + "\n";
        }
        return patientAsText;
    }

    @Step("Create {0} patients")
    public void createSomePatiens(int numberOfPatients) throws RequestException {
        FakeNameGenerator aFakeNameGenerator = new FakeNameGenerator();
        for (int i = 0; i < numberOfPatients; i++) {
            Patient patient = getPatientData(aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(false) + "@werfen.com");
            Patient patientCreated = clientForPatient.create(patient);
            TestUtil.printReturnedId("Patient", patientCreated.getId());
        }
    }

    @TmsLink("SEED-SCC-3")
    @Link(value = "REQ-PAT-3", type = "requirement")
    @DisplayName("Test get a Patient List.")
    @Description("Test that is possible to get a list of patients.")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetPatientList() throws RequestException {

        createSomePatiens(5);

        List<Patient> patientsBefore = clientForPatient.get();
        Assertions.assertNotNull(patientsBefore);
        int initialSize = patientsBefore.size();

        savePatientsDatabase(patientsBefore);

        createSomePatiens(5);

        List<Patient> patientsAfter = clientForPatient.get();
        Assertions.assertNotNull(patientsAfter);
        int finalSize = patientsAfter.size();
        savePatientsDatabase(patientsAfter);

        TestUtil.checkANumber("Check that the new list size is", initialSize + 5, finalSize);
    }

    @TmsLink("SEED-SCC-4")
    @Link(value = "REQ-PAT-4", type = "requirement")
    @DisplayName("Test get a Patient.")
    @Description("Test that it is possible to get a patient.")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetPatient() throws RequestException {

        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = clientForPatient.create(patient);
        TestUtil.printReturnedId("Patient", patientCreated.getId());
        Patient patientRetrieved = clientForPatient.get(patientCreated.getId());
        TestUtil.checkObjectIsNotNull("patient", patientRetrieved);
        TestUtil.checkField("Name", "John", patientRetrieved.getName());
        TestUtil.checkField("Surname", "Burrows", patientRetrieved.getSurname());
        TestUtil.checkField("Email", "jburrows@werfen.com", patientRetrieved.getEmail());
    }

    @TmsLink("SEED-SCC-5")
    @Link(value = "REQ-PAT-5", type = "requirement")
    @DisplayName("Test get non-existing Patient.")
    @Description("Test that if I try to get an non-existing patient, I get a 404.")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetUnexistingPatient() throws RequestException {

        Patient patientRetrieved;
        Exception caughtException = null;
        try {
            patientRetrieved = clientForPatient.get(new Long(23434534).longValue());
        } catch (Exception ex) {
            caughtException = ex;
        }
        TestUtil.checkObjectIsNotNull("Exception", caughtException);
        TestUtil.checkThatIHaveAnExceptionException(404, ((RequestException) caughtException).getErrorCode());
    }

    @TmsLink("SEED-SCC-6")
    @Link(value = "REQ-PAT-6", type = "requirement")
    @DisplayName("Test delete a Patient.")
    @Description("Test that is possible to delete a patient.")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testDeletePatient() throws RequestException {
        Patient patient = getPatientData("John", "Burrows", "jburrows@werfen.com");
        Patient patientCreated = clientForPatient.create(patient);
        TestUtil.checkObjectIsNotNull("patient", patientCreated);
        boolean result = clientForPatient.delete(patientCreated.getId());
        TestUtil.checkResultIsTrue(result);
    }

    @TmsLink("SEED-SCC-7")
    @Link(value = "REQ-PAT-7", type = "requirement")
    @DisplayName("Test delete non-existing Patient.")
    @Description("Test that if I try to delete an non-existing patient, I get a 404.")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testDeleteUnexistingPatient() throws RequestException {
        boolean result = false;
        Exception caughtException = null;
        try {
            result = clientForPatient.delete(new Long(34324).longValue());
        } catch (Exception ex) {
            caughtException = ex;
        }
        TestUtil.checkObjectIsNotNull("Exception", caughtException);
        TestUtil.checkThatIHaveAnExceptionException(404, ((RequestException) caughtException).getErrorCode());
    }
}