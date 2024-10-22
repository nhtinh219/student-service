package com.kienlong.api.studentservice.security.auth;

import com.kienlong.api.studentservice.i18n.MessageCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class AuthRequest {

    @NotNull(message = "{username.not-null}")
    @Length(min = 5, max = 20, message = "{username.size}")
    private String username;

    @NotNull(message = "{password.not-null}")
    @Length(min = 5, max = 10, message = "{password.size}")
    private String password;


}
