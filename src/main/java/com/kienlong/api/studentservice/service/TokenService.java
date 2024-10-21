package com.kienlong.api.studentservice.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.kienlong.api.studentservice.entity.RefreshToken;
import com.kienlong.api.studentservice.error.RefreshTokenExpiredException;
import com.kienlong.api.studentservice.error.RefreshTokenNotFoundException;
import com.kienlong.api.studentservice.repo.RefreshTokenRepository;
import com.kienlong.api.studentservice.entity.User;
import com.kienlong.api.studentservice.security.auth.AuthResponse;
import com.kienlong.api.studentservice.security.auth.RefreshTokenRequest;
import com.kienlong.api.studentservice.security.jwt.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepo;
    @Autowired
    JwtUtility jwtUtility;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Value("${app.security.jwt.refresh-token.expiration}")
    private int refreshTokenExpiration;

    public AuthResponse generateTokens(User user) {
        String accessToken = jwtUtility.generateAccessToken(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);

        // Generate refresh token by a random UUID
        String randomUUID = UUID.randomUUID().toString();
        authResponse.setRefreshToken(randomUUID);

        // For persist refresh token into DB
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(passwordEncoder.encode(randomUUID));
        refreshToken.setUser(user);

        long refreshTokenExpirationInMillis = refreshTokenExpiration * 60000L + System.currentTimeMillis();
        refreshToken.setExpiryTime(new Date(refreshTokenExpirationInMillis));

        refreshTokenRepo.save(refreshToken);

        return authResponse;
    }

    public AuthResponse refreshTokens(RefreshTokenRequest request)
            throws RefreshTokenNotFoundException, RefreshTokenExpiredException {

        String rawRefreshToken = request.getRefreshToken();
        List<RefreshToken> refreshTokenList = refreshTokenRepo.findByUsername(request.getUsername());

        RefreshToken foundRefreshToken = null;

        for (RefreshToken token : refreshTokenList) {
            if (passwordEncoder.matches(rawRefreshToken, token.getToken())) {
                foundRefreshToken = token;
            }
        }
        if (foundRefreshToken == null) {
            throw new RefreshTokenNotFoundException();
        }
        Date currentTime = new Date();
        if (foundRefreshToken.getExpiryTime().before(currentTime)) {
            throw new RefreshTokenExpiredException();
        }
        AuthResponse response = generateTokens(foundRefreshToken.getUser());

        refreshTokenRepo.delete(foundRefreshToken);

        return response;
    }

}
