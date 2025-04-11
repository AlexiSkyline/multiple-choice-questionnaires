package org.skyline.mcq.infrastructure.inputadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.SignUpRequestDto;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.domain.models.Result;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.skyline.mcq.infrastructure.outputport.ResultRepository;
import org.skyline.mcq.infrastructure.outputport.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ResultAPITest {

    private static final String RESULT_PATH = "/api/v1/results";
    private static final String RESULT_PATH_ID = RESULT_PATH + "/{resultId}";
    private static final String RESULT_PATH_SURVEY_ID = RESULT_PATH + "/survey/{surveyId}";
    private static final String RESULT_PATH_ACCOUNT_ID = RESULT_PATH_SURVEY_ID + "/account/{accountId}";
    private static final String RESULT_PATH_MY_ACCOUNT = RESULT_PATH + "/account";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResultAPI resultAPI;

    @Autowired
    private MockMvc mockMvc;

    private static String tokenRespondent;
    private static String tokenCreator;
    private static UUID surveyId;
    private static UUID resultId;
    private static UUID accountId;

    @BeforeAll
    static void initializeTestEnvironment(
            @Autowired AuthAPI authAPI,
            @Autowired AccountRepository accountRepository,
            @Autowired ResultRepository resultRepository,
            @Autowired CategoryRepository categoryRepository,
            @Autowired SurveyRepository surveyRepository
    ) {
        var testRespondent = buildSignUpRequest("respondentResult", "lastname admin", "respondent.result.test@gmail.com");
        var testCreator = buildSignUpRequest("creatorResult", "lastname creator", "creator.result.test@gmail.com");

        tokenRespondent = registerUserAndGetToken(authAPI, testRespondent, TypeRole.ROLE_SURVEY_RESPONDENT);
        tokenCreator = registerUserAndGetToken(authAPI, testCreator, TypeRole.ROLE_SURVEY_CREATOR);

        var category = categoryRepository.findAll().getFirst();

        var creator = accountRepository.findByEmail(testCreator.getEmail()).orElseThrow();
        var survey = createTestSurvey(surveyRepository, creator, category);
        surveyId = survey.getId();

        var respondent = accountRepository.findByEmail(testRespondent.getEmail()).orElseThrow();
        accountId = respondent.getId();
        resultId = createTestResult(resultRepository, respondent, survey);
    }

    private static Survey createTestSurvey(SurveyRepository repository, Account account, Category category) {
        return repository.save(Survey.builder()
                .title("test title")
                .description("test description")
                .image("test image")
                .maxPoints(10)
                .questionCount(5)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .category(category)
                .account(account)
                .status(true)
                .build());
    }

    private static UUID createTestResult(ResultRepository repository, Account account, Survey survey) {
        Result result = Result.builder()
                .account(account)
                .survey(survey)
                .startTime(Timestamp.valueOf(LocalDateTime.now()))
                .endTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)))
                .duration(1800)
                .totalPoints(10)
                .correctAnswers(5)
                .incorrectAnswers(0)
                .build();

        return repository.save(result).getId();
    }

    private static SignUpRequestDto buildSignUpRequest(String firstName, String lastName, String email) {
        return SignUpRequestDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(firstName)
                .email(email)
                .password("password")
                .build();
    }

    private static String registerUserAndGetToken(AuthAPI authAPI, SignUpRequestDto request, TypeRole role) {
        return Objects.requireNonNull(authAPI.registerUserWithRole(request, role).getBody()).getAccessToken();
    }

    private ResultActions performAuthorizedRequest(HttpMethod method, String path, String token, Object body, Object... uriVars) throws Exception {
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
    @Rollback
    @Transactional
    @DisplayName("(RESPONDENT) Get Result by ID: Should return 200 OK")
    void testGetResultById() throws Exception {
        performAuthorizedRequest(HttpMethod.GET, RESULT_PATH_ID, tokenRespondent, null, resultId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data").isNotEmpty()
                );
    }

    @Test
    @DisplayName("(RESPONDENT) Get Result by ID: Should return 404 Not Found")
    void testGetResultByIdNotFound() throws Exception {
        performAuthorizedRequest(HttpMethod.GET, RESULT_PATH_ID, tokenRespondent, null, UUID.randomUUID())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    @DisplayName("(CREATOR) Get Results by Survey ID: Should return 200 OK")
    void testGetResultBySurveyId() throws Exception {
        performAuthorizedRequest(HttpMethod.GET, RESULT_PATH_SURVEY_ID, tokenCreator, null, surveyId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.content").isArray()
                );
    }

    @Test
    @DisplayName("(CREATOR) Get Results by Survey ID: Should return 404 Not Found")
    void testGetResultBySurveyIdNotFound() throws Exception {
        performAuthorizedRequest(HttpMethod.GET, RESULT_PATH_SURVEY_ID, tokenCreator, null, UUID.randomUUID())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    @DisplayName("(RESPONDENT) Get all result of my account: Should return 200 OK")
    void testGetAllResultOfAccount() throws Exception {
        performAuthorizedRequest(HttpMethod.GET, RESULT_PATH_MY_ACCOUNT, tokenRespondent, null)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.content").isArray()
                );
    }

    @Test
    @DisplayName("(RESPONDENT) Get Result by Survey Id and Account Id: Should return 200 OK")
    void testGetResultBySurveyIdAndAccountId() throws Exception {
        performAuthorizedRequest(HttpMethod.GET, RESULT_PATH_ACCOUNT_ID, tokenRespondent, null, surveyId, accountId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.content").isArray()
                );
    }

    @Test
    @DisplayName("Validate account exists: should not throw exception")
    void testValidateAccountExists() {
        assertDoesNotThrow(() -> resultAPI.validateAccountExists(accountId));
    }

    @Test
    @DisplayName("Validate account does not exist: should throw NotFoundException")
    void validateAccountNotExist() {
        UUID invalidAccountId = UUID.randomUUID();

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                resultAPI.validateAccountExists(invalidAccountId)
        );

        assertAll(
                () -> assertFalse(ex.getMessage().isEmpty()),
                () -> assertTrue(ex.getMessage().contains(invalidAccountId.toString())),
                () -> assertEquals("Account with identifier '" + invalidAccountId + "' was not found. Please provide a valid account ID", ex.getMessage())
        );
    }
}
