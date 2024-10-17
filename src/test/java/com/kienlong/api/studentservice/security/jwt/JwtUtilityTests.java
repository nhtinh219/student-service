package com.kienlong.api.studentservice.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kienlong.api.studentservice.entity.User;
import com.kienlong.api.studentservice.error.JwtValidationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class JwtUtilityTests {

    private static JwtUtility jwtUtil;

    @BeforeAll
    static void setup() {
        jwtUtil = new JwtUtility();
        jwtUtil.setIssuerName("My Company");
        jwtUtil.setSecretKey("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuv!@#$%^&*()");
        jwtUtil.setAccessTokenExpiration(2);
    }

    @Test
    public void testGenerateFail() {
        assertThrows(IllegalArgumentException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                User user = null;
                jwtUtil.generateAccessToken(user);
            }
        });
    }

    @Test
    public void testGenerateSuccess() {
        User user = new User();
        user.setId(3);
        user.setRole("read");
        user.setUsername("Tinh");

        String token = jwtUtil.generateAccessToken(user);

        assertThat(token).isNotNull();

        System.out.println(token);
    }

    @Test
    public void testValidateFail() {
        assertThrows(JwtValidationException.class, () -> {
            jwtUtil.validateAccessToken("a.b.c");
        });
    }

    @Test
    public void testValidateSuccess() {
        User user = new User();
        user.setId(3);
        user.setRole("read");
        user.setUsername("Tinh");

        String token = jwtUtil.generateAccessToken(user);

        assertThat(token).isNotNull();

        assertDoesNotThrow(() -> {
            jwtUtil.validateAccessToken(token);
        });
    }

}
