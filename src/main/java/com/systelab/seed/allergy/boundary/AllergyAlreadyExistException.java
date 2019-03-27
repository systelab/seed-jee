package com.systelab.seed.allergy.boundary;

import com.systelab.seed.BaseException;

public class AllergyAlreadyExistException extends BaseException {
    public AllergyAlreadyExistException() {
        super(ErrorCode.ALLERGY_ALREADY_EXIST);
    }
}
