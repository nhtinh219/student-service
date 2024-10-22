package com.kienlong.api.studentservice.security.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {

	private String accessToken;

	private String refreshToken;

}
