package com.systelab.seed.patient.boundary.bean;

import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.infrastructure.pagination.Pageable;
import com.systelab.seed.patient.boundary.PatientNotFoundException;
import com.systelab.seed.patient.boundary.PatientService;
import com.systelab.seed.patient.control.PatientWorkbookGenerator;
import com.systelab.seed.patient.control.cdi.PatientCreated;
import com.systelab.seed.patient.entity.Patient;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.UUID;

@Stateless
public class PatientServiceBean implements PatientService {

    @PersistenceContext(unitName = "SEED")
    private EntityManager em;

    @Inject
    @PatientCreated
    private Event<Patient> patientCreated;

    @Inject
    private PatientWorkbookGenerator workBookGenerator;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Patient patient) {
        em.persist(patient);
        // Be careful because CDI Events are synchronous.
        // In JEE 8 fireAsync was introduced. Use it as soon as you upgrade.
        patientCreated.fire(patient);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Patient update(UUID id, Patient patient) {
        em.merge(patient);
        return patient;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(UUID id) throws PatientNotFoundException {
        Patient p = em.find(Patient.class, id);
        if (p != null) {
            em.remove(p);
        } else {
            throw new PatientNotFoundException();
        }
    }

    @Override
    public Page<Patient> getAllPatients(Pageable pageable) {

        TypedQuery<Long> queryTotal = em.createNamedQuery(Patient.ALL_COUNT, Long.class);
        TypedQuery<Patient> query = em.createNamedQuery(Patient.FIND_ALL, Patient.class);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        return new Page<>(query.getResultList(), queryTotal.getSingleResult());
    }

    @Override
    public void deactivatePatientsBefore(LocalDateTime modificationTime) {
        Query query = em.createNamedQuery(Patient.DEACTIVATE);
        query.setParameter("modificationTime", modificationTime);
        query.executeUpdate();
    }

    @Override
    public XSSFWorkbook getPatientsWorkbook() {
        return workBookGenerator.getWorkbook(getAllPatients(new Pageable()));
    }

    @Override
    public Patient getPatient(UUID patientId) {
        return em.find(Patient.class, patientId);
    }
}
