package com.systelab.seed.patientallergy.boundary.bean;

import com.systelab.seed.allergy.boundary.AllergyNotFoundException;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.patient.boundary.PatientNotFoundException;
import com.systelab.seed.patient.entity.Patient;
import com.systelab.seed.patientallergy.boundary.PatientAllergyService;
import com.systelab.seed.patientallergy.entity.PatientAllergy;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Set;
import java.util.UUID;

@Stateless
public class PatientAllergyServiceBean implements PatientAllergyService {

    @PersistenceContext(unitName = "SEED")
    private EntityManager em;


    @Override
    public Set<PatientAllergy> getPatientAllergies(UUID patientId) throws PatientNotFoundException {
        Patient patient = em.find(Patient.class, patientId);
        if (patient != null) {
            return patient.getAllergies();
        } else {
            throw new PatientNotFoundException();
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PatientAllergy addPatientAllergy(UUID patientId, PatientAllergy patientAllergy) throws PatientNotFoundException, AllergyNotFoundException {
        Patient patient = em.find(Patient.class, patientId);
        Allergy allergy = em.find(Allergy.class, patientAllergy.getAllergy().getId());

        if (patient == null) {
            throw new PatientNotFoundException();
        }
        if (allergy == null) {
            throw new AllergyNotFoundException();
        }
        PatientAllergy a = new PatientAllergy(patient, allergy);
        a.setNote(patientAllergy.getNote());
        a.setAssertedDate(patientAllergy.getAssertedDate());
        a.setLastOccurrence(patientAllergy.getLastOccurrence());
        em.merge(a);
        em.flush();
        return a;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removePatientAllergy(UUID patientId, UUID allergyId) throws PatientNotFoundException, AllergyNotFoundException {
        Patient patient = em.find(Patient.class, patientId);
        Allergy allergy = em.find(Allergy.class, allergyId);

        if (patient == null) {
            throw new PatientNotFoundException();
        }
        if (allergy == null) {
            throw new AllergyNotFoundException();
        }
        PatientAllergy a = new PatientAllergy(patient, allergy);
        em.remove(a);
    }
}
