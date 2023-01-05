package com.systelab.seed;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SeedException wraps all checked standard Java exception and enriches them with a custom error code.
 * You can use this code to retrieve localized error messages and to link to our online documentation.
 */
public class BaseException extends Exception {

    private static final long serialVersionUID = 3714428835173293220L;
    private final ErrorCode errorCode;

    @AllArgsConstructor
    @Getter
    public enum ErrorCode {
        ALLERGY_ALREADY_EXIST(5),
        ALLERGY_NOT_FOUND(4),
        PATIENT_NOT_FOUND(3),
        USER_NOT_FOUND(2),
        DEFAULT_ERROR(1);

       private final int code;
    }

    public BaseException(ErrorCode code) {
        super();
        this.errorCode = code;
    }

    public BaseException(String message, Throwable cause, ErrorCode code) {
        super(message, cause);
        this.errorCode = code;
    }

    public ErrorCode getCode() {
        return this.errorCode;
    }
}
