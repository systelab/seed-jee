package com.systelab.seed.client;

public class RequestException extends Exception {
    private int errorCode = 0;

    public RequestException(int errorCode) {
        super("HTTP Request Exception");
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
