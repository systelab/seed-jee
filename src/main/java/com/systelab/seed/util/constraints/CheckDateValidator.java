package com.systelab.seed.util.constraints;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckDateValidator implements ConstraintValidator<CheckDate, String> {

    private String dateFormat = "";

    @Override
    public void initialize(CheckDate constraintAnnotation) {
        if (constraintAnnotation.dateFormat() == null)
            dateFormat = "";
        else
            dateFormat = constraintAnnotation.dateFormat();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validate(value);
    }

    private boolean validate(String dateString) {
        switch (dateFormat.toUpperCase()) {
            case "YYYY-MM-DD":
                return isValidDate(dateString, "yyyy-MM-dd");
            case "YYYY-MM":
                return isValidDate(dateString, "yyyy-MM");
            case "YYYY":
                return isValidDate(dateString, "yyyy");
            case "YYYYMM":
                return isValidDate(dateString, "yyyyMM");
            case "YYYYMMDD":
                return isValidDate(dateString, "yyyyMMdd");
            case "HHMM":
                return isValidDate(dateString, "HHmm");
            default:
                return false;
        }
    }

    private boolean isValidDate(String dateString, String pattern) {
        boolean valid = true;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            sdf.parse(dateString.trim());
        } catch (ParseException e) {
            valid = false;
        }
        return valid;
    }
}