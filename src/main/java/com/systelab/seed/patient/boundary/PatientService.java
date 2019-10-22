package com.systelab.seed.patient.boundary;

import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.infrastructure.pagination.Pageable;
import com.systelab.seed.patient.entity.Patient;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ejb.Local;
import java.time.LocalDateTime;
import java.util.UUID;

@Local
public interface PatientService {

    Page<Patient> getAllPatients(Pageable pageable);

    XSSFWorkbook getPatientsWorkbook();

    Patient getPatient(UUID patientId);

    void create(Patient patient);

    Patient update(UUID id, Patient patient);

    void delete(UUID id) throws PatientNotFoundException;

    void deactivatePatientsBefore(LocalDateTime modificationTime);
}