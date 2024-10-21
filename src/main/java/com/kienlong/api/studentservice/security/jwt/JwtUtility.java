package com.kienlong.api.studentservice.security.jwt;

import java.util.Date;
import java.util.Locale;

import javax.crypto.spec.SecretKeySpec;

import com.kienlong.api.studentservice.entity.User;
import com.kienlong.api.studentservice.error.JwtValidationException;
import com.kienlong.api.studentservice.i18n.MessageCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtility {
    private static final String SECRET_KEY_ALGORITHM = "HmacSHA512";

    @Autowired
    MessageSource messageSource;

    @Value("${app.security.jwt.issuer}")
    private String issuerName;

    @Value("${app.security.jwt.secret}")
    private String secretKey;

    @Value("${app.security.jwt.access-token.expiration}")
    private int accessTokenExpiration;

    public String generateAccessToken(User user) {
        if (user == null || user.getId() == null || user.getUsername() == null
                || user.getRole() == null) {
            throw new IllegalArgumentException("User object is null or its fields have null values");
        }

        long expirationTimeInMillis = System.currentTimeMillis() + accessTokenExpiration * 60000L;
        String subject = String.format("%s,%s", user.getId(), user.getUsername());

        return Jwts.builder()
                .header().add("typ", "JWT")
                .and()
                .subject(subject)
                .issuer(issuerName)
                .issuedAt(new Date())
                .expiration(new Date(expirationTimeInMillis))
                .claim("role", user.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), Jwts.SIG.HS512)
                .compact();
    }

    public Claims validateAccessToken(String token, HttpServletRequest request) throws JwtValidationException {
        Locale locale = null;
        if (request != null) {
            String lang = request.getHeader("Accept-Language");
            if (lang != null){
                locale = new Locale(lang);
            }
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), SECRET_KEY_ALGORITHM);

            return Jwts.parser()
                    .verifyWith(keySpec)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            throw new JwtValidationException(this.getMessage(MessageCode.ERROR_TOKEN_EXPIRED.getCode(), locale), ex);
        } catch (IllegalArgumentException ex) {
            throw new JwtValidationException(this.getMessage(MessageCode.ERROR_TOKEN_ILLEGAL.getCode(), locale), ex);
        } catch (MalformedJwtException ex) {
            throw new JwtValidationException(this.getMessage(MessageCode.ERROR_ACCESS_TOKEN_NOT_WELL_FORMED.getCode(), locale), ex);
        } catch (UnsupportedJwtException ex) {
            throw new JwtValidationException(this.getMessage(MessageCode.ERROR_ACCESS_TOKEN_NOT_SUPPORTED.getCode(),locale), ex);
        }
    }

    private String getMessage(String code, Locale locale){
        return messageSource.getMessage(code, null, locale);
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setAccessTokenExpiration(int accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

}
