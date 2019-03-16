package com.systelab.seed.allergy.boundary;

import com.systelab.seed.BaseException;

public class AllergyNotFoundException extends BaseException {
    public AllergyNotFoundException() {
        super(ErrorCode.ALLERGY_NOT_FOUND);
    }
}
