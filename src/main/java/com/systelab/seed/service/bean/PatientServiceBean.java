package com.systelab.seed.service.bean;

import com.systelab.seed.infrastructure.events.cdi.PatientCreated;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.service.PatientService;
import com.systelab.seed.util.exceptions.PatientNotFoundException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

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
        // Be careful because CDI Events are synchronous.
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
        TypedQuery<Patient> query = em.createNamedQuery(Patient.FIND_ALL, Patient.class);
        return query.getResultList();
    }

    @Override
    public XSSFWorkbook getPatientsWorkbook() {
        final XSSFWorkbook wb = new XSSFWorkbook();

        Sheet sheet = wb.createSheet("Patients");
        List<Patient> patients = getAllPatients();

        int rowNum = 0;

        for (int i = 0; i < patients.size(); i++) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            Cell cell1 = row.createCell(colNum++);
            cell1.setCellValue(patients.get(i).getName());
            Cell cell2 = row.createCell(colNum++);
            cell2.setCellValue(patients.get(i).getSurname());
            Cell cell3 = row.createCell(colNum++);
            cell3.setCellValue(patients.get(i).getEmail());
        }
        return wb;
    }

    @Override
    public Patient getPatient(Long patientId) {
        return em.find(Patient.class, patientId);
    }

}
