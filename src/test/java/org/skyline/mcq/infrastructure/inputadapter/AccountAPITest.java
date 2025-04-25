package org.skyline.mcq.infrastructure.inputadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.input.SignUpRequestDto;
import org.skyline.mcq.domain.enums.TypeRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AccountAPITest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final String ACCOUNT_PATH = "/api/v1/accounts";
    private static String token;

    @BeforeAll
    static void initializeTestEnvironment(@Autowired AuthAPI authAPI) {
        var testCreator = SignUpRequestDto.builder()
                .firstName("creatorAccount")
                .lastName("lastname creator")
                .username("creatorAccount")
                .email("creator.account.test@gmail.com")
                .password("password")
                .build();

        token = registerUserAndGetToken(authAPI, testCreator);
    }

    private static String registerUserAndGetToken(AuthAPI authAPI, SignUpRequestDto request) {
        return Objects.requireNonNull(authAPI.registerUserWithRole(request, TypeRole.ROLE_SURVEY_CREATOR).getBody()).getAccessToken();
    }

    private ResultActions performAuthorizedRequest(HttpMethod method, String path, Object body, Object... uriVars) throws Exception {
        var request = MockMvcRequestBuilders.request(method, path, uriVars)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if (body != null) {
            request.content(objectMapper.writeValueAsString(body));
        }

        return mockMvc.perform(request);
    }

    @Test
    @DisplayName("Get profile user: Should return 200 OK")
    void testGetProfile() throws Exception {
        performAuthorizedRequest(HttpMethod.GET, ACCOUNT_PATH, null)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update profile user: Should return 200 OK")
    void testUpdateAccountInfo() throws Exception {
        AccountProfileUpdateDto accountProfileUpdateDto = AccountProfileUpdateDto.builder()
                .description("Update description")
                .firstName("Update first name")
                .lastName("Update last name")
                .build();

        performAuthorizedRequest(HttpMethod.PUT, ACCOUNT_PATH, accountProfileUpdateDto)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.firstName").value(accountProfileUpdateDto.getFirstName()),
                        jsonPath("$.data.lastName").value(accountProfileUpdateDto.getLastName())
                );
    }

    @Test
    @DisplayName("Update profile user: Should return 400 Bad Request")
    void testUpdateAccountInfoBadRequest() throws Exception {
        AccountProfileUpdateDto accountProfileUpdateDto = AccountProfileUpdateDto.builder().build();

        performAuthorizedRequest(HttpMethod.PUT, ACCOUNT_PATH, accountProfileUpdateDto)
                .andExpect(status().isBadRequest());
    }
}