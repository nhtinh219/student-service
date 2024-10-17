package com.kienlong.api.studentservice.error;

import java.io.Serial;

public class JwtValidationException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }


}
