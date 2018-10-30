package com.systelab.seed.patient.boundary;

import com.systelab.seed.BaseException;

public class PatientNotFoundException extends BaseException {
    public PatientNotFoundException() {
        super(ErrorCode.PATIENT_NOT_FOUND);
    }
}
