package com.systelab.seed.patient.control;

import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.patient.entity.Patient;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PatientWorkbookGenerator {

    public XSSFWorkbook getWorkbook(Page<Patient> patients) {
        final XSSFWorkbook wb = new XSSFWorkbook();

        Sheet sheet = wb.createSheet("Patients");
        for (int i = 0; i < patients.getContent().size(); i++) {
            addPatientToWorkBook(sheet, i, patients.getContent().get(i));
        }
        return wb;
    }

    private void addPatientToWorkBook(Sheet sheet, int rowIndex, Patient patient) {
        int colNum = 0;

        Row row = sheet.createRow(rowIndex);
        createCell(row, colNum++, patient.getName());
        createCell(row, colNum++, patient.getSurname());
        createCell(row, colNum++, patient.getEmail());
    }

    private void createCell(Row row, int colIndex, String value) {
        Cell cell1 = row.createCell(colIndex);
        cell1.setCellValue(value);
    }
}
