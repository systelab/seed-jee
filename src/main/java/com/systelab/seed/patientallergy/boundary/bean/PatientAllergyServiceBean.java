package com.systelab.seed.patientallergy.boundary.bean;

import com.systelab.seed.allergy.boundary.AllergyAlreadyExistException;
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
    public PatientAllergy addPatientAllergy(UUID patientId, PatientAllergy patientAllergy) throws PatientNotFoundException, AllergyNotFoundException, AllergyAlreadyExistException {
        Patient patient = em.find(Patient.class, patientId);
        Allergy allergy = em.find(Allergy.class, patientAllergy.getAllergy().getId());

        if (patient == null) {
            throw new PatientNotFoundException();
        }
        if (allergy == null) {
            throw new AllergyNotFoundException();
        }
        PatientAllergy patientAllergyToStore = new PatientAllergy(patient, allergy);
        patientAllergyToStore.setNote(patientAllergy.getNote());
        patientAllergyToStore.setAssertedDate(patientAllergy.getAssertedDate());
        patientAllergyToStore.setLastOccurrence(patientAllergy.getLastOccurrence());

        if (em.find(PatientAllergy.class, patientAllergyToStore.getId()) == null) {
            em.merge(patientAllergyToStore);
        } else {
            throw new AllergyAlreadyExistException();
        }
        return patientAllergyToStore;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PatientAllergy updatePatientAllergy(UUID patientId, UUID allergyId, PatientAllergy patientAllergy) throws PatientNotFoundException, AllergyNotFoundException {
        Patient patient = em.find(Patient.class, patientId);
        Allergy allergy = em.find(Allergy.class, allergyId);

        if (patient == null) {
            throw new PatientNotFoundException();
        }
        if (allergy == null) {
            throw new AllergyNotFoundException();
        }
        PatientAllergy patientAllergyToStore = new PatientAllergy(patient, allergy);
        patientAllergyToStore.setNote(patientAllergy.getNote());
        patientAllergyToStore.setAssertedDate(patientAllergy.getAssertedDate());
        patientAllergyToStore.setLastOccurrence(patientAllergy.getLastOccurrence());
        em.merge(patientAllergyToStore);
        return patientAllergyToStore;
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
        PatientAllergy patientAllergyToRemove = new PatientAllergy(patient, allergy);
        PatientAllergy patientAllergy = em.find(PatientAllergy.class, patientAllergyToRemove.getId());
        if (patientAllergy != null)
            em.remove(patientAllergy);
    }
}
