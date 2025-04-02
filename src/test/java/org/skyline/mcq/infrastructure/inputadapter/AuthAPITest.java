package org.skyline.mcq.infrastructure.inputadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.LoginRequestDto;
import org.skyline.mcq.application.dtos.input.RefreshTokenRequestDto;
import org.skyline.mcq.application.dtos.input.SignUpRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthAPITest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private SignUpRequestDto creatorSignUpRequest;
    private SignUpRequestDto respondentSignUpRequest;
    private static final RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto();
    private static LoginRequestDto validLoginRequest;
    private static String validAccessToken;
    private static String validRefreshToken;

    private static final String AUTH_BASE_PATH = "/api/v1/auth";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        creatorSignUpRequest = SignUpRequestDto.builder()
                .firstName("John")
                .lastName("Smith")
                .username("johnsmith")
                .email("john.smith@gmail.com")
                .password("password")
                .build();

        respondentSignUpRequest = SignUpRequestDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .username("janedoe")
                .email("janedoe@gmail.com")
                .password("password")
                .build();
    }

    @BeforeAll
    static void setUpTestUser(@Autowired AuthAPI authAPI) {
        SignUpRequestDto testCreator = SignUpRequestDto.builder()
                .firstName("John99")
                .lastName("Smith99")
                .username("johnsmith99")
                .email("john99.smith@gmail.com")
                .password("password")
                .build();

        authAPI.registerCreator(testCreator);

        validLoginRequest = LoginRequestDto.builder()
                .email("john99.smith@gmail.com")
                .password("password")
                .build();

        var loginResponse = Objects.requireNonNull(authAPI.login(validLoginRequest).getBody());
        validRefreshToken = loginResponse.getRefreshToken();
        validAccessToken = loginResponse.getAccessToken();

        refreshTokenRequest.setRefreshToken(validRefreshToken);
    }

    @Test
    @DisplayName("Should register new creator successfully and return tokens")
    void testRegisterCreator() throws Exception {
        mockMvc.perform(post(AUTH_BASE_PATH + "/register/creator")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creatorSignUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("Should register new respondent successfully and return tokens")
    void testRegisterRespondent() throws Exception {
        mockMvc.perform(post(AUTH_BASE_PATH + "/register/respondent")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(respondentSignUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("Should authenticate user and return tokens")
    void testLogin() throws Exception {
        mockMvc.perform(post(AUTH_BASE_PATH + "/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("Should return unauthorized when invalid credentials")
    void testLoginInvalidCredentials() throws Exception {
        LoginRequestDto invalidRequest = LoginRequestDto.builder()
                .email("invalid@email.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post(AUTH_BASE_PATH + "/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return same refresh token when logging in again")
    void testLoginShouldReturnSameRefreshToken() throws Exception {
        mockMvc.perform(post(AUTH_BASE_PATH + "/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").value(validRefreshToken));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void testRefreshTokenShouldReturnNewTokens() throws Exception {
        mockMvc.perform(post(AUTH_BASE_PATH + "/refresh-token")
                        .header("Authorization", "Bearer " + validAccessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("Should refresh token even with expired access token")
    void testRefreshTokenAccessTokenExpired() throws Exception {
        String expiredAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huOTkuc21pdGhAZ21haWwuY29tIiwiaWF0IjoxNzQzNDg4MzAxLCJleHAiOjE3NDM0OTE5MDF9.F4Cdy12gBPSIDFAJaw7_svypCZRGSM5Zs85cDyOcYYI";

        mockMvc.perform(post(AUTH_BASE_PATH + "/refresh-token")
                        .header("Authorization", "Bearer " + expiredAccessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").exists());
    }
}