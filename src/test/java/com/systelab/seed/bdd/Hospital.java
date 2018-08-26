package com.systelab.seed.bdd;

import com.systelab.seed.model.patient.Patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for BDD Test purposes (it is not part of the seed class model)
 */
public class Hospital {

    private List<Patient> patients = new ArrayList<>();

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    public List<Patient> findPatients(final LocalDate from, final LocalDate to) {
        return patients.stream()
                .filter(patient -> patient.getDob().isAfter(from) && patient.getDob().isBefore(to))
                .sorted(Comparator.comparing(Patient::getDob).reversed())
                .collect(Collectors.toList());
    }
}
