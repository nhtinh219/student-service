package com.kienlong.api.studentservice.security.auth;

import com.kienlong.api.studentservice.i18n.MessageCode;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public class AuthRequest {

    @NotNull(message = "{username.not-null}")
    @Length(min = 5, max = 20, message = "{username.size}")
    private String username;

    @NotNull(message = "{password.not-null}")
    @Length(min = 5, max = 10, message = "{password.size}")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
