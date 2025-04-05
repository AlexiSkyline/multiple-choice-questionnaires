package org.skyline.mcq.infrastructure.inputadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.SignUpRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.infrastructure.inputport.AccountInputPort;
import org.skyline.mcq.infrastructure.inputport.SurveyInputPort;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SurveyAPITest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    Account accountTest;
    Category categoryTest;
    SurveyRequestDto surveyRequestDtoTest;
    SurveyUpdateRequestDto surveyUpdateRequestDtoTest;

    private static final String SURVEY_PATH = "/api/v1/surveys";
    private static final String SURVEY_PATH_ID = SURVEY_PATH + "/{surveyId}";
    private static final String SURVEY_QUESTION_PATH_ID = SURVEY_PATH + "/{surveyId}/questions";
    private static final String SURVEY_ACCOUNT_PATH_ID = SURVEY_PATH + "/{surveyId}/accounts";

    private static final String TEST_TITLE = "new title";
    private static final String TEST_DESCRIPTION = "new description";
    private static final String TEST_IMAGE = "survey.png";
    private static final int TEST_MAX_POINTS = 10;
    private static final int TEST_QUESTION_COUNT = 5;
    private static final int TEST_TIME_LIMIT = 3600;

    private static String tokenCreator;
    private static String tokenAdmin;
    private static String tokenRespondent;
    private static UUID surveyIdA;
    private static UUID surveyIdB;

    @BeforeAll
    static void initializeTestEnvironment(
            @Autowired AuthAPI authAPI,
            @Autowired AccountInputPort accountInputPort,
            @Autowired SurveyInputPort surveyInputPort,
            @Autowired CategoryRepository categoryRepository) {

        tokenCreator = createAndAuthenticateUser(authAPI, "Creator", TypeRole.ROLE_SURVEY_CREATOR);
        tokenAdmin = createAndAuthenticateUser(authAPI, "Admin", TypeRole.ROLE_ADMIN);
        tokenRespondent = createAndAuthenticateUser(authAPI, "Respondent", TypeRole.ROLE_SURVEY_RESPONDENT);

        UUID categoryId = categoryRepository.findAll().getFirst().getId();
        accountInputPort.getUserByEmail("creator.userTest.smith.test@gmail.com").ifPresent(account -> {
            surveyIdA = createTestSurvey(surveyInputPort, account.getId(), categoryId);
            surveyIdB = createTestSurvey(surveyInputPort, account.getId(), categoryId);
        });
    }

    @BeforeEach
    void setupTestData() {
        this.accountTest = accountRepository.findAll().getFirst();
        this.categoryTest = categoryRepository.findAll().getFirst();
        this.surveyRequestDtoTest = createDefaultSurveyRequest();
        this.surveyUpdateRequestDtoTest = createDefaultSurveyUpdateRequest();
    }

    private static String createAndAuthenticateUser(AuthAPI authAPI, String rolePrefix, TypeRole role) {
        SignUpRequestDto request = SignUpRequestDto.builder()
                .firstName(rolePrefix + "UserTest")
                .lastName("lastname " + rolePrefix.toLowerCase())
                .username(rolePrefix + "UserTest")
                .email(rolePrefix.toLowerCase() + ".userTest.smith.test@gmail.com")
                .password("password")
                .build();

        return Objects.requireNonNull(authAPI.registerUserWithRole(request, role).getBody()).getAccessToken();
    }

    private static UUID createTestSurvey(SurveyInputPort port, UUID accountId, UUID categoryId) {
        SurveyRequestDto request = SurveyRequestDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .image(TEST_IMAGE)
                .maxPoints(TEST_MAX_POINTS)
                .questionCount(TEST_QUESTION_COUNT)
                .timeLimit(TEST_TIME_LIMIT)
                .attempts(1)
                .hasRestrictedAccess(true)
                .categoryId(categoryId)
                .status(true)
                .build();

        return port.saveSurvey(accountId, request).map(SurveyResponseDto::getId).orElseThrow();
    }

    private SurveyRequestDto createDefaultSurveyRequest() {
        return SurveyRequestDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .image(TEST_IMAGE)
                .maxPoints(TEST_MAX_POINTS)
                .questionCount(TEST_QUESTION_COUNT)
                .timeLimit(TEST_TIME_LIMIT)
                .attempts(1)
                .hasRestrictedAccess(true)
                .categoryId(categoryTest.getId())
                .status(true)
                .build();
    }

    private SurveyUpdateRequestDto createDefaultSurveyUpdateRequest() {
        return SurveyUpdateRequestDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .image(TEST_IMAGE)
                .maxPoints(TEST_MAX_POINTS)
                .questionCount(TEST_QUESTION_COUNT)
                .timeLimit(TEST_TIME_LIMIT)
                .attempts(1)
                .hasRestrictedAccess(true)
                .status(true)
                .build();
    }

    private ResultActions performAuthorizedRequest(HttpMethod method, String path, String token, Object content, Object... uriVars) throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(method, path, uriVars)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if (content != null) {
            request.content(objectMapper.writeValueAsString(content));
        }

        return mockMvc.perform(request);
    }

    @Test
    @DisplayName("Save Survey: should save a survey and return 201 Created")
    void testSaveSurvey() throws Exception {

        performAuthorizedRequest(HttpMethod.POST, SURVEY_PATH, tokenCreator, surveyRequestDtoTest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.data.id").exists(),
                        jsonPath("$.data.title").value(TEST_TITLE)
                );
    }

    @Test
    @DisplayName("Save Survey: should return 400 Bad Request when any field is null")
    void testSaveSurveyBadRequest() throws Exception {

        surveyRequestDtoTest.setTitle(null);
        performAuthorizedRequest(HttpMethod.POST, SURVEY_PATH, tokenCreator, surveyRequestDtoTest)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.httpError").value(400),
                        jsonPath("$.httpStatus").value("BAD_REQUEST")
                );
    }

    @Test
    @DisplayName("Save Survey: should return 404 Not Found when account or category is invalid")
    void testSaveSurveyNotFound() throws Exception {

        surveyRequestDtoTest.setCategoryId(UUID.randomUUID());
        performAuthorizedRequest(HttpMethod.POST, SURVEY_PATH, tokenCreator, surveyRequestDtoTest)
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    @DisplayName("Get All Surveys: should return 200 OK")
    void getAllSurveys() throws Exception {

        performAuthorizedRequest(HttpMethod.GET, SURVEY_PATH, tokenRespondent, null)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content.length()").isNotEmpty()
                );
    }

    @Test
    @DisplayName("(Creator) Get All Surveys: should return 200 OK")
    void getAllSurveysCreator() throws Exception {

        performAuthorizedRequest(HttpMethod.GET, SURVEY_PATH + "/creator", tokenCreator, null)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content.length()").isNotEmpty()
                );
    }

    @Test
    @DisplayName("(Admin) Get All Surveys: should return 200 OK")
    void getAllSurveysAdmin() throws Exception {

        performAuthorizedRequest(HttpMethod.GET, SURVEY_PATH + "/admin", tokenAdmin, null)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content.length()").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Get Survey By ID: should return 200 OK")
    void getSurveyById() throws Exception {

        performAuthorizedRequest(HttpMethod.GET, SURVEY_PATH_ID, tokenRespondent, null, surveyIdA)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.title", is(surveyRequestDtoTest.getTitle())),
                        jsonPath("$.data.description", is(surveyRequestDtoTest.getDescription())),
                        jsonPath("$.data.image", is(surveyRequestDtoTest.getImage()))
                );
    }

    @Test
    @DisplayName("Get Survey By ID: should return 404 Not Found when survey ID is invalid")
    void getSurveyByIdNotFound() throws Exception {

        performAuthorizedRequest(HttpMethod.GET, SURVEY_PATH_ID, tokenRespondent, null, UUID.randomUUID())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    @DisplayName("Get Survey Questions: should return 200 OK")
    void getSurveyQuestions() throws Exception {

        performAuthorizedRequest(HttpMethod.GET, SURVEY_QUESTION_PATH_ID, tokenRespondent, null, surveyIdA)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data").isArray()
                );
    }

    @Test
    @DisplayName("Get Survey Questions: should return 404 Not Found when survey ID is invalid")
    void getSurveyQuestionsNotFound() throws Exception {

        performAuthorizedRequest(HttpMethod.GET, SURVEY_QUESTION_PATH_ID, tokenRespondent, null, UUID.randomUUID())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    @DisplayName("Update Survey: should return 204 No Content")
    void updateSurvey() throws Exception {

        performAuthorizedRequest(HttpMethod.PUT, SURVEY_PATH_ID, tokenCreator, surveyUpdateRequestDtoTest, surveyIdA)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Update Survey: should return 404 Not Found when survey ID is invalid")
    void updateSurveyNotFound() throws Exception {

        performAuthorizedRequest(HttpMethod.PUT, SURVEY_PATH_ID, tokenCreator, surveyUpdateRequestDtoTest, UUID.randomUUID())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    @DisplayName("Delete Survey: should return 204 No Content")
    void deleteSurvey() throws Exception {

        performAuthorizedRequest(HttpMethod.DELETE, SURVEY_PATH_ID, tokenCreator, null, surveyIdB)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete Survey: should return 404 Not Found when survey ID is invalid")
    void deleteSurveyNotFound() throws Exception {

        performAuthorizedRequest(HttpMethod.DELETE, SURVEY_PATH_ID, tokenCreator, null,  UUID.randomUUID())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    @DisplayName("Get accounts: should return all accounts related to a survey")
    void getAccountsBySurveyId() throws Exception {

        performAuthorizedRequest(HttpMethod.GET, SURVEY_ACCOUNT_PATH_ID, tokenCreator, null, surveyIdA)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.content").isArray()
                );
    }

    @Test
    @DisplayName("(Admin) Get accounts: should return all accounts related to a survey")
    void getAccountsBySurveyIdAdmin() throws Exception {

        performAuthorizedRequest(HttpMethod.GET, SURVEY_ACCOUNT_PATH_ID + "/admin", tokenAdmin, null, surveyIdA)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content").isEmpty()
                );
    }
}