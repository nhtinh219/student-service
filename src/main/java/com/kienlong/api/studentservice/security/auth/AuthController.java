package com.kienlong.api.studentservice.security.auth;

import com.kienlong.api.studentservice.error.RefreshTokenExpiredException;
import com.kienlong.api.studentservice.error.RefreshTokenNotFoundException;
import com.kienlong.api.studentservice.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/oauth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    TokenService tokenService;
    @Autowired
    AuthenticationManager authManager;

    @PostMapping("/token")
    public ResponseEntity<?> getAccessToken(@RequestBody @Valid AuthRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AuthResponse response = tokenService.generateTokens(userDetails.getUser());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        try {
            AuthResponse authResponse = tokenService.refreshTokens(request);

            return ResponseEntity.ok(authResponse);
        } catch (RefreshTokenNotFoundException | RefreshTokenExpiredException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
