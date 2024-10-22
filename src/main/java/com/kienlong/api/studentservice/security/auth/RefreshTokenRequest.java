package com.kienlong.api.studentservice.security.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class RefreshTokenRequest {
    @NotNull(message = "{username.not-null}")
    @Length(min = 5, max = 20, message = "{username.size}")
    private String username;

    @NotNull(message = "{refresh-token.not-null}")
    @Length(min = 36, max = 50, message = "{refresh-token.size}")
    private String refreshToken;
}
