package com.systelab.seed.bdd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.systelab.seed.model.patient.Patient;
import cucumber.api.Format;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that defines the expected behavior for BDD Test following Gherkin language
 */
public class PatientSearchSteps {

	Hospital hospital = new Hospital();
	List<Patient> result = new ArrayList<>();

	@Given(".+patient with the name '(.+)', surname '(.+)', born in (.+)")
	public void addNewPatient(final String name, final String surname, @Format("dd MMMMM yyyy") final Date born) {
		Patient patient = new Patient();
		patient.setName(name);
		patient.setSurname(surname);
		patient.setDob(born);
		hospital.addPatient(patient);
	}

	@When("^the user searches for patients born between (\\d+) and (\\d+)$")
	public void setSearchParameters(@Format("yyyy") final Date from, @Format("yyyy") final Date to) {
		result = hospital.findPatients(from, to);
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
