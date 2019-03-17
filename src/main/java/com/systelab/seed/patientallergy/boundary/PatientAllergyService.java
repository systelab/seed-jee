package com.systelab.seed.patientallergy.boundary;

import com.systelab.seed.allergy.boundary.AllergyNotFoundException;
import com.systelab.seed.patient.boundary.PatientNotFoundException;
import com.systelab.seed.patientallergy.entity.PatientAllergy;

import javax.ejb.Local;
import java.util.Set;
import java.util.UUID;

@Local
public interface PatientAllergyService {

    Set<PatientAllergy> getPatientAllergies(UUID patientId) throws PatientNotFoundException;

    PatientAllergy addPatientAllergy(UUID patientId, PatientAllergy patientAllergy) throws PatientNotFoundException, AllergyNotFoundException;

    void removePatientAllergy(UUID patientId, UUID allergyId) throws PatientNotFoundException, AllergyNotFoundException;
}