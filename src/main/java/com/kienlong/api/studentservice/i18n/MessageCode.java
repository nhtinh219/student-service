package com.kienlong.api.studentservice.i18n;

public enum MessageCode {
    ERROR_TOKEN_EXPIRED("error.access-token.expired"),
    ERROR_TOKEN_ILLEGAL("error.access-token.illegal"),
    ERROR_ACCESS_TOKEN_NOT_WELL_FORMED("error.access-token.not-well-formed"),
    ERROR_ACCESS_TOKEN_NOT_SUPPORTED("error.access-token.not-supported"),
    ERROR_AUTH_REQUIRED("error.auth.required"),
    ERROR_ACCESS_DENIED("error.access.denied"),
    ERROR_BAD_CREDENTIALS("error.bad-credentials");

    private final String code;

    MessageCode(String code) {
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
