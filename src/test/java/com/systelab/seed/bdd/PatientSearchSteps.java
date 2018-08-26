package com.systelab.seed.bdd;

import com.systelab.seed.model.patient.Patient;
import cucumber.api.Format;
import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Class that defines the expected behavior for BDD Test following Gherkin language
 */

@Feature("Cucumber Test Suite")
@DisplayName("Cucumber Test Suite")
public class PatientSearchSteps {

    private Hospital hospital = new Hospital();
    private List<Patient> result = new ArrayList<>();

    @Given(".+patient with the name '(.+)', surname '(.+)', born in (.+)")
    public void addNewPatient(final String name, final String surname, @Format("dd MMMMM yyyy") @Transform(DateMapper.class) final LocalDate born) {
        Patient patient = new Patient();
        patient.setName(name);
        patient.setSurname(surname);
        patient.setDob(born);
        hospital.addPatient(patient);
    }

    @When("^the user searches for patients born between (\\d+) and (\\d+)$")
    public void setSearchParameters(final int yearFrom, final int yearTo) {
        result = hospital.findPatients(LocalDate.of(yearFrom, 1, 1), LocalDate.of(yearTo, 12, 31));
    }

    @Then("(\\d+) patients should have been found$")
    public void verifyAmountOfPatientsFound(final int patientsFound) {
        assertThat(result.size(), equalTo(patientsFound));
    }

    @Then("Patient (\\d+) should have the name '(.+)'$")
    public void verifyPatientAtPosition(final int position, final String name) {
        assertThat(result.get(position - 1).getName(), equalTo(name));
    }
}
