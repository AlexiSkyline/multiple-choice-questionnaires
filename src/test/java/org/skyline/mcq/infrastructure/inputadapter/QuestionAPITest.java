package org.skyline.mcq.infrastructure.inputadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.QuestionRequestDto;
import org.skyline.mcq.application.dtos.input.QuestionUpdateRequestDto;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.skyline.mcq.infrastructure.outputport.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class QuestionAPITest {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionAPI questionAPI;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private static final String QUESTION_PATH = "/api/v1/questions";
    private static final String QUESTION_PATH_ID = QUESTION_PATH + "/{questionId}";

    private QuestionRequestDto questionRequestDtoTest;
    private QuestionUpdateRequestDto questionUpdateRequestDtoTest;
    private Survey surveyTest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        surveyTest = createSurvey();
        questionRequestDtoTest = createQuestionRequestDto();
        questionUpdateRequestDtoTest = createQuestionUpdateRequestDto();
    }

    private Survey createSurvey() {
        var accountTest = accountRepository.findAll().getFirst();
        var categoryTest = categoryRepository.findAll().getFirst();

        return Survey.builder()
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(false)
                .active(true)
                .account(accountTest)
                .category(categoryTest)
                .status(true)
                .build();
    }

    private QuestionRequestDto createQuestionRequestDto() {
        return QuestionRequestDto.builder()
                .content("Test Question")
                .image("Test Image")
                .points(10)
                .allowedAnswers(1)
                .options("Option 1, Option 2, Option 3, Option 4")
                .correctAnswers("Option 1")
                .build();
    }

    private QuestionUpdateRequestDto createQuestionUpdateRequestDto() {
        return QuestionUpdateRequestDto.builder()
                .content("Test Question update")
                .image("Test Image update")
                .points(10)
                .allowedAnswers(1)
                .options("Option 1, Option 2, Option 3, Option 4")
                .correctAnswers("Option 2")
                .build();
    }

    private ResultActions performPostRequest(Object content) throws Exception {
        return mockMvc.perform(post(QuestionAPITest.QUESTION_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    private ResultActions performGetRequest(Object... uriVariables) throws Exception {
        return mockMvc.perform(get(QuestionAPITest.QUESTION_PATH_ID, uriVariables)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPutRequest(Object content, Object... uriVariables) throws Exception {
        return mockMvc.perform(put(QuestionAPITest.QUESTION_PATH_ID, uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    private ResultActions performDeleteRequest(Object... uriVariables) throws Exception {
        return mockMvc.perform(delete(QuestionAPITest.QUESTION_PATH_ID, uriVariables)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Create Question: Should create a question and return 201 Created")
    void testSaveQuestion() throws Exception {
        var newSurvey = surveyRepository.save(surveyTest);
        questionRequestDtoTest.setSurveyId(newSurvey.getId());

        performPostRequest(questionRequestDtoTest)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Create Question: Should return 404 Not Found when survey does not exist")
    void testSaveQuestionNotFound() throws Exception {
        questionRequestDtoTest.setSurveyId(UUID.randomUUID());

        performPostRequest(questionRequestDtoTest)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create Question: Should return 400 Bad Request when any field is null")
    void testSaveQuestionBadRequest() throws Exception {
        questionRequestDtoTest.setContent(null);

        performPostRequest(questionRequestDtoTest)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create Question: Should return 409 Conflict when survey has reached the maximum number of questions")
    void testSaveQuestionConflict() throws Exception {
        surveyTest.setQuestionCount(0);
        var newSurvey = surveyRepository.save(surveyTest);
        questionRequestDtoTest.setSurveyId(newSurvey.getId());

        performPostRequest(questionRequestDtoTest)
                .andExpect(status().isConflict());
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Get Question by ID: Should return a question and 200 OK")
    void testGetQuestionById() throws Exception {
        var newSurvey = surveyRepository.save(surveyTest);
        questionRequestDtoTest.setSurveyId(newSurvey.getId());
        var newQuestion = Objects.requireNonNull(questionAPI.saveQuestion(questionRequestDtoTest).getBody()).data();

        performGetRequest(newQuestion.getId())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Question by ID: Should return 404 Not Found when question does not exist")
    void testGetQuestionByIdNotFound() throws Exception {
        performGetRequest(UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Update Question: Should update a question and return 204 No Content")
    void testUpdateQuestion() throws Exception {
        var newSurvey = surveyRepository.save(surveyTest);
        questionRequestDtoTest.setSurveyId(newSurvey.getId());
        var newQuestion = Objects.requireNonNull(questionAPI.saveQuestion(questionRequestDtoTest).getBody()).data();

        performPutRequest(questionUpdateRequestDtoTest, newQuestion.getId())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Update Question: Should return 404 Not Found when question does not exist")
    void testUpdateQuestionNotFound() throws Exception {
        performPutRequest(questionUpdateRequestDtoTest, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update Question: Should return 400 Bad Request when any field is null")
    void testUpdateQuestionBadRequest() throws Exception {
        questionUpdateRequestDtoTest.setContent(null);

        performPutRequest(questionUpdateRequestDtoTest, UUID.randomUUID())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete Question: Should delete a question and return 204 No Content")
    void testDeleteQuestion() throws Exception {
        var newSurvey = surveyRepository.save(surveyTest);
        questionRequestDtoTest.setSurveyId(newSurvey.getId());
        var newQuestion = Objects.requireNonNull(questionAPI.saveQuestion(questionRequestDtoTest).getBody()).data();

        performDeleteRequest(newQuestion.getId())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete Question: Should return 404 Not Found when question does not exist")
    void testDeleteQuestionNotFound() throws Exception {
        performDeleteRequest(UUID.randomUUID())
                .andExpect(status().isNotFound());
    }
}