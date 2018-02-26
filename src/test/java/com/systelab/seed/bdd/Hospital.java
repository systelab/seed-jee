package com.systelab.seed.bdd;

import com.systelab.seed.model.patient.Patient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for BDD Test purposes (it is not part of the seed class model)
 */
public class Hospital {

	private List<Patient> patients = new ArrayList<>();

	public void addPatient(Patient patient) {
		patients.add(patient);
	}

	public List<Patient> findPatients(final Date from, final Date to) {
		Calendar end = Calendar.getInstance();
		end.setTime(to);
		end.roll(Calendar.YEAR, 1);

		return patients.stream().filter(patient -> {
			return from.before(patient.getDob()) && end.getTime().after(patient.getDob());})
				.sorted(Comparator.comparing(Patient::getDob).reversed())
				.collect(Collectors.toList());
	}
}
