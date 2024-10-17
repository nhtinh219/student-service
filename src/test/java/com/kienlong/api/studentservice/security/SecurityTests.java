package com.kienlong.api.studentservice.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kienlong.api.studentservice.security.auth.RefreshTokenRequest;
import com.kienlong.api.studentservice.entity.Student;
import com.kienlong.api.studentservice.security.auth.AuthRequest;
import com.kienlong.api.studentservice.security.auth.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {
    private static final String GET_ACCESS_TOKEN_ENDPOINT = "/api/oauth/token";
    private static final String LIST_STUDENT_ENDPOINT = "/api/students?pageSize=10&pageNum=1";
    private static final String REFRESH_TOKEN_ENDPOINT = "/api/oauth/token/refresh";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @Test
    public void getBaseUriShouldReturn401() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testGetAccessTokenBadRequest() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("");
        request.setPassword("");

        String bodyContent = mapper.writeValueAsString(request);

        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .contentType("application/json").content(bodyContent))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAccessTokenFail() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("tinhnh");
        request.setPassword("aaaaaa");

        String bodyContent = mapper.writeValueAsString(request);

        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .contentType("application/json").content(bodyContent))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAccessTokenSuccess() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("tinhnh");
        request.setPassword("nhtinh");

        String bodyContent = mapper.writeValueAsString(request);

        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .contentType("application/json").content(bodyContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    public void testListStudentFail() throws Exception {
        mockMvc.perform(get(LIST_STUDENT_ENDPOINT).header("Authorization", "Bearer somethinginvalid"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error[0]").isNotEmpty())
                .andDo(print());
    }

    @Test
    public void testListStudentSuccess() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("tinhnh");
        request.setPassword("nhtinh");

        String bodyContent = mapper.writeValueAsString(request);
        MvcResult mvcResult = mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .contentType("application/json").content(bodyContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        AuthResponse authResponse = mapper.readValue(responseBody, AuthResponse.class);

        String bearerToken = "Bearer " + authResponse.getAccessToken();

        mockMvc.perform(get(LIST_STUDENT_ENDPOINT).header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").isString())
                .andDo(print());
    }

    @Test
    public void testAddStudent1() throws Exception {
        String apiEndpoint = "/api/students";

        Student student = new Student();
        student.setName("Tiger 1");

        String requestBody = mapper.writeValueAsString(student);

        //Have to change authority to SCOPE_write in SecurityConfig
        mockMvc.perform(post(apiEndpoint).contentType("application/json").content(requestBody)
                        .with(jwt().jwt(jwt -> jwt.claim("scope", "write"))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void testAddStudent2() throws Exception {
        String apiEndpoint = "/api/students";

        Student student = new Student();
        student.setName("Tiger 2");

        String requestBody = mapper.writeValueAsString(student);

        mockMvc.perform(post(apiEndpoint).contentType("application/json").content(requestBody)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_write"))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void testUpdateStudent1() throws Exception {
        String apiEndpoint = "/api/students";

        Student student = new Student();
        student.setId(4);
        student.setName("Panther");

        //Have to change authority to SCOPE_write in SecurityConfig
        Jwt jwt = Jwt.withTokenValue("xxx")
                .header("alg", "none")
                .issuer("Kien Long")
                .claim("scope", "write")
                .subject("1,tinhnh")
                .build();

        String requestBody = mapper.writeValueAsString(student);

        mockMvc.perform(put(apiEndpoint).contentType("application/json").content(requestBody)
                        .with(jwt().jwt(jwt)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void testUpdateStudent2() throws Exception {
        String apiEndpoint = "/api/students";

        Student student = new Student();
        student.setId(4);
        student.setName("Panzer IV");

        Jwt jwt = Jwt.withTokenValue("xxx")
                .header("alg", "none")
                .issuer("Kien Long")
                .subject("1,tinhnh")
                .build();

        String requestBody = mapper.writeValueAsString(student);

        //Have to change authority to SCOPE_write in SecurityConfig
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("SCOPE_write");

        var token = new JwtAuthenticationToken(jwt, authorities);

        mockMvc.perform(put(apiEndpoint).contentType("application/json").content(requestBody)
                        .with(authentication(token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void testDeleteStudent() throws Exception {
        int studentId = 6;
        String apiEndpoint = "/api/students/" + studentId;

        mockMvc.perform(delete(apiEndpoint).with(jwt().authorities(new SimpleGrantedAuthority("write"))))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void testRefreshTokenBadRequest() throws Exception {
        String refreshToken = "";
        String username = "";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);
        request.setUsername(username);

        String requestContent = mapper.writeValueAsString(request);

        mockMvc.perform(post(REFRESH_TOKEN_ENDPOINT).contentType("application/json").content(requestContent))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRefreshTokenFail() throws Exception {
        String refreshToken = "1234567891234567891234567891234567890";
        String username = "admin";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);
        request.setUsername(username);

        String requestContent = mapper.writeValueAsString(request);

        mockMvc.perform(post(REFRESH_TOKEN_ENDPOINT).contentType("application/json").content(requestContent))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRefreshTokenSuccess() throws Exception {
        String refreshToken = "7353f626-baec-4d13-8bd9-9e1d0aad3b32";
        String username = "tinhnh";

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);
        request.setUsername(username);

        String requestContent = mapper.writeValueAsString(request);

        mockMvc.perform(post(REFRESH_TOKEN_ENDPOINT).contentType("application/json").content(requestContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }
}
