package org.skyline.mcq.infrastructure.inputadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.QuestionRequestDto;
import org.skyline.mcq.application.dtos.input.QuestionUpdateRequestDto;
import org.skyline.mcq.application.dtos.input.SignUpRequestDto;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.infrastructure.inputport.QuestionInputPort;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
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

import java.util.Objects;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionAPITest {

    @Autowired
    private QuestionInputPort questionInputPort;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final String QUESTION_PATH = "/api/v1/questions";
    private static final String QUESTION_PATH_ID = QUESTION_PATH + "/{questionId}";

    private QuestionRequestDto questionRequestDtoTest;
    private QuestionUpdateRequestDto questionUpdateRequestDtoTest;
    private static String tokenCreator;
    private static UUID surveyId;
    private static UUID surveyBId;

    @BeforeAll
    static void initializeTestEnvironment(
            @Autowired AuthAPI authAPI,
            @Autowired AccountRepository accountRepository,
            @Autowired CategoryRepository categoryRepository,
            @Autowired SurveyRepository surveyRepository
    ) {
        var testCreator = buildSignUpRequest();

        tokenCreator = registerUserAndGetToken(authAPI, testCreator);

        var category = categoryRepository.findAll().getFirst();

        var creator = accountRepository.findByEmail(testCreator.getEmail()).orElseThrow();
        var survey = createTestSurvey(surveyRepository, creator, category, 5);
        surveyId = survey.getId();

        var surveyB = createTestSurvey(surveyRepository, creator, category, 0);
        surveyBId = surveyB.getId();
    }

    @BeforeEach
    void setUp() {
        questionRequestDtoTest = createQuestionRequestDto();
        questionUpdateRequestDtoTest = createQuestionUpdateRequestDto();
    }

    private static Survey createTestSurvey(SurveyRepository repository, Account account, Category category, int numberQuestion) {
        return repository.save(Survey.builder()
                .title("test title")
                .description("test description")
                .image("test image")
                .maxPoints(10)
                .questionCount(numberQuestion)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .category(category)
                .account(account)
                .status(true)
                .build());
    }

    private QuestionRequestDto createQuestionRequestDto() {
        return QuestionRequestDto.builder()
                .content("Test Question")
                .image("Test Image")
                .points(10)
                .allowedAnswers(1)
                .options("{\"optionA\":\"Refresco\",\"optionB\":\"Agua\",\"optionC\":\"Cerveza\",\"optionD\":\"Jugo\"}")
                .correctAnswers("{\"optionA\":\"Refresco\",\"optionC\":\"Cerveza\"}")
                .build();
    }

    private QuestionUpdateRequestDto createQuestionUpdateRequestDto() {
        return QuestionUpdateRequestDto.builder()
                .content("Test Question update")
                .image("Test Image update")
                .points(10)
                .allowedAnswers(1)
                .options("{\"optionA\":\"Refresco\",\"optionB\":\"Agua\",\"optionC\":\"Cerveza\",\"optionD\":\"Jugo\"}")
                .correctAnswers("{\"optionA\":\"Refresco\",\"optionC\":\"Cerveza\"}")
                .build();
    }

    private static SignUpRequestDto buildSignUpRequest() {
        return SignUpRequestDto.builder()
                .firstName("creatorQuestion")
                .lastName("lastname creator")
                .username("creatorQuestion")
                .email("creator.1uestion.test@gmail.com")
                .password("password")
                .build();
    }

    private static String registerUserAndGetToken(AuthAPI authAPI, SignUpRequestDto request) {
        return Objects.requireNonNull(authAPI.registerUserWithRole(request, TypeRole.ROLE_SURVEY_CREATOR).getBody()).getAccessToken();
    }

    private ResultActions performAuthorizedRequest(HttpMethod method, String path, Object body, Object... uriVars) throws Exception {
        var request = MockMvcRequestBuilders.request(method, path, uriVars)
                .header("Authorization", "Bearer " + tokenCreator)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if (body != null) {
            request.content(objectMapper.writeValueAsString(body));
        }

        return mockMvc.perform(request);
    }

    @Test
    @DisplayName("Create Question: Should create a question and return 201 Created")
    void testSaveQuestion() throws Exception {
        questionRequestDtoTest.setSurveyId(surveyId);
        performAuthorizedRequest(HttpMethod.POST, QuestionAPITest.QUESTION_PATH, questionRequestDtoTest)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Create Question: Should return 404 Not Found when survey does not exist")
    void testSaveQuestionNotFound() throws Exception {
        questionRequestDtoTest.setSurveyId(UUID.randomUUID());

        performAuthorizedRequest(HttpMethod.POST, QuestionAPITest.QUESTION_PATH, questionRequestDtoTest)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create Question: Should return 400 Bad Request when any field is null")
    void testSaveQuestionBadRequest() throws Exception {
        questionRequestDtoTest.setContent(null);

        performAuthorizedRequest(HttpMethod.POST, QuestionAPITest.QUESTION_PATH, questionRequestDtoTest)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create Question: Should return 409 Conflict when survey has reached the maximum number of questions")
    void testSaveQuestionConflict() throws Exception {
        questionRequestDtoTest.setSurveyId(surveyBId);
        performAuthorizedRequest(HttpMethod.POST, QuestionAPITest.QUESTION_PATH, questionRequestDtoTest)
                .andExpect(status().isConflict());
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Get Question by ID: Should return a question and 200 OK")
    void testGetQuestionById() throws Exception {
        questionRequestDtoTest.setSurveyId(surveyId);
        var newQuestion = questionInputPort.saveQuestion(questionRequestDtoTest).orElseThrow();
        performAuthorizedRequest(HttpMethod.GET, QuestionAPITest.QUESTION_PATH_ID, null, newQuestion.getId())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Question by ID: Should return 404 Not Found when question does not exist")
    void testGetQuestionByIdNotFound() throws Exception {
        performAuthorizedRequest(HttpMethod.GET, QuestionAPITest.QUESTION_PATH_ID, null, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Update Question: Should update a question and return 204 No Content")
    void testUpdateQuestion() throws Exception {
        questionRequestDtoTest.setSurveyId(surveyId);
        var newQuestion = questionInputPort.saveQuestion(questionRequestDtoTest).orElseThrow();
        performAuthorizedRequest(HttpMethod.PUT, QuestionAPITest.QUESTION_PATH_ID, questionUpdateRequestDtoTest,  newQuestion.getId())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Update Question: Should return 404 Not Found when question does not exist")
    void testUpdateQuestionNotFound() throws Exception {
        performAuthorizedRequest(HttpMethod.PUT, QuestionAPITest.QUESTION_PATH_ID, questionUpdateRequestDtoTest, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update Question: Should return 400 Bad Request when any field is null")
    void testUpdateQuestionBadRequest() throws Exception {
        questionUpdateRequestDtoTest.setContent(null);

        performAuthorizedRequest(HttpMethod.PUT, QuestionAPITest.QUESTION_PATH_ID, questionUpdateRequestDtoTest, UUID.randomUUID())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete Question: Should delete a question and return 204 No Content")
    void testDeleteQuestion() throws Exception {
        questionRequestDtoTest.setSurveyId(surveyId);
        var newQuestion = questionInputPort.saveQuestion(questionRequestDtoTest).orElseThrow();

        performAuthorizedRequest(HttpMethod.DELETE, QuestionAPITest.QUESTION_PATH_ID, questionUpdateRequestDtoTest, newQuestion.getId())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete Question: Should return 404 Not Found when question does not exist")
    void testDeleteQuestionNotFound() throws Exception {
        performAuthorizedRequest(HttpMethod.DELETE, QuestionAPITest.QUESTION_PATH_ID, null, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }
}