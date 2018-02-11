package com.systelab.seed.service.bean;

import com.systelab.seed.infrastructure.events.cdi.PatientCreated;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.service.PatientService;
import com.systelab.seed.util.exceptions.PatientNotFoundException;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class PatientServiceBean implements PatientService {

    @PersistenceContext(unitName = "SEED")
    private EntityManager em;


    @Inject
    @PatientCreated
    private Event<Patient> patientCreated;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Patient patient) {
        em.persist(patient);
        // TODO: Be careful because CDI Events are synchronous.
        // In JEE 8 fireAsync was introduced. Use it as soon as you upgrade.
        patientCreated.fire(patient);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Patient update(Long id, Patient patient) {
        em.merge(patient);
        return patient;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) throws PatientNotFoundException {
        Patient p = em.find(Patient.class, id);
        if (p != null) {
            em.remove(p);
        } else {
            throw new PatientNotFoundException();
        }
    }


    @Override
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<Patient>();

        TypedQuery<Patient> query = em.createNamedQuery(Patient.FIND_ALL, Patient.class);
        List<Patient> patientList = query.getResultList();
        for (Patient patient : patientList) {
            patients.add(patient);
        }
        return patients;
    }

    @Override
    public Patient getPatient(Long patientId) {
        Patient p = em.find(Patient.class, patientId);
        return p;
    }

}
