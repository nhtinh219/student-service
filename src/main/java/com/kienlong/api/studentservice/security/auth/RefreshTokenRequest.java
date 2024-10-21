package com.kienlong.api.studentservice.security.auth;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class RefreshTokenRequest {
    @NotNull(message = "{username.not-null}")
    @Length(min = 5, max = 20, message = "{username.size}")
    private String username;

    @NotNull(message = "{refresh-token.not-null}")
    @Length(min = 36, max = 50, message = "{refresh-token.size}")
    private String refreshToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
