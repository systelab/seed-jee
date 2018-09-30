package com.systelab.seed.unit;

import com.systelab.seed.FakeNameGenerator;
import com.systelab.seed.TestUtil;
import com.systelab.seed.client.PatientClient;
import com.systelab.seed.client.RequestException;
import com.systelab.seed.model.patient.Address;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.util.pagination.Page;
import io.qameta.allure.*;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.*;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.joining;

@TmsLink("TC0001_PatientManagement_IntegrationTest")
@Feature("Patient Test Suite.\n\nGoal:\nThis test case is intended to verify the correct ....\n\nEnvironment:\n...\nPreconditions:\nN/A.")
@DisplayName("Patients Test Suite")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PatientClientTest extends BaseClientTest {
    private static final Logger logger = Logger.getLogger(PatientClientTest.class.getName());

    public static PatientClient clientForPatient;

    @BeforeAll
    public static void init() throws RequestException {
        clientForPatient = new PatientClient();
        login(clientForPatient);
        logger.log(Level.INFO, clientForPatient.getServerURL());
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

    @DisplayName("Create a Patient.")
    @Description("Action: Create a patient with name, surname and email and check that the values are stored.")
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
        TestUtil.checkThatIHaveAnException(400, ((RequestException) caughtException).getErrorCode());
    }

    @DisplayName("Create an invalid Patient.")
    @Description("Action: Create a patient with invalid data and check that we get an exception.")
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
        return patients.stream().map((patient) -> patient.getSurname() + ", " + patient.getName() + "\t" + patient.getEmail()).collect(joining("\n"));
    }

    @Step("Create {0} patients")
    public void createSomePatients(int numberOfPatients) throws RequestException {
        FakeNameGenerator aFakeNameGenerator = new FakeNameGenerator();
        for (int i = 0; i < numberOfPatients; i++) {
            Patient patient = getPatientData(aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(false) + "@werfen.com");
            Patient patientCreated = clientForPatient.create(patient);
            TestUtil.printReturnedId("Patient", patientCreated.getId());
        }
    }

    @DisplayName("Get a Patient List.")
    @Description("Action: Get a list of patients, and check that are all the patient of the DB")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetPatientList() throws RequestException {

        createSomePatients(5);

        Page<Patient> patientsBefore = clientForPatient.get();
        Assertions.assertNotNull(patientsBefore);
        long initialSize = patientsBefore.getTotalElements();
        savePatientsDatabase(patientsBefore.getContent());
        createSomePatients(5);

        Page<Patient> patientsAfter = clientForPatient.get();
        Assertions.assertNotNull(patientsAfter);
        long finalSize = patientsAfter.getTotalElements();
        savePatientsDatabase(patientsAfter.getContent());

        TestUtil.checkANumber("The new list size is", initialSize + 5, finalSize);
    }

    @DisplayName("Get a Patient.")
    @Description("Action: Get a patient by id, an check that the data is correct.")
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

    @DisplayName("Get a non-existing Patient.")
    @Description("Action: Get a patient with an non-existing id and check that we get the appropriate error.")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetUnexistingPatient() throws RequestException {

        Exception caughtException = null;
        try {
            clientForPatient.get(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d")
            );
        } catch (Exception ex) {
            caughtException = ex;
        }
        TestUtil.checkObjectIsNotNull("Exception", caughtException);
        TestUtil.checkThatIHaveAnException(404, ((RequestException) caughtException).getErrorCode());
    }

    @DisplayName("Delete a Patient.")
    @Description("Action: Delete a patient by id and check that we get an ok.")
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

    @DisplayName("Delete non-existing Patient.")
    @Description("Action: Delete a patient with an non-existing id and check that we get the appropriate error.")
    @Tag("patient")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testDeleteUnexistingPatient() throws RequestException {
        Exception caughtException = null;
        try {
            clientForPatient.delete(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
        } catch (Exception ex) {
            caughtException = ex;
        }
        TestUtil.checkObjectIsNotNull("Exception", caughtException);
        TestUtil.checkThatIHaveAnException(404, ((RequestException) caughtException).getErrorCode());
    }
}