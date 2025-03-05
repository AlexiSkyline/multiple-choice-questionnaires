package org.skyline.mcq.infrastructure.inputadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.SurveyRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyUpdateRequestDto;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SurveyAPITest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    SurveyAPI surveyAPI;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    Account accountTest;
    Category categoryTest;
    SurveyRequestDto surveyRequestDtoTest;
    SurveyUpdateRequestDto surveyUpdateRequestDtoTest;

    private static final String SURVEY_PATH = "/api/v1/surveys";
    private static final String SURVEY_PATH_ID = SURVEY_PATH + "/{surveyId}";
    private static final String SURVEY_QUESTION_PATH_ID = SURVEY_PATH + "/questions/{surveyId}";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        accountTest = accountRepository.findAll().getFirst();
        categoryTest = categoryRepository.findAll().getFirst();
        surveyRequestDtoTest = createSurveyRequestDto();
        surveyUpdateRequestDtoTest = createSurveyUpdateRequestDto();
    }

    private SurveyRequestDto createSurveyRequestDto() {
        return SurveyRequestDto.builder()
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .accountId(accountTest.getId())
                .categoryId(categoryTest.getId())
                .status(true)
                .build();
    }

    private SurveyUpdateRequestDto createSurveyUpdateRequestDto() {
        return SurveyUpdateRequestDto.builder()
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .status(true)
                .build();
    }

    private ResultActions performPostRequest(Object content) throws Exception {
        return mockMvc.perform(post(SurveyAPITest.SURVEY_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    private ResultActions performGetRequest(String path, Object... uriVariables) throws Exception {
        return mockMvc.perform(get(path, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPutRequest(String path, Object content, Object... uriVariables) throws Exception {
        return mockMvc.perform(put(path, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    private ResultActions performDeleteRequest(Object... uriVariables) throws Exception {
        return mockMvc.perform(delete(SurveyAPITest.SURVEY_PATH_ID, uriVariables)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Save Survey: should save a survey and return 201 Created")
    void testSaveSurvey() throws Exception {

        performPostRequest(surveyRequestDtoTest)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Save Survey: should return 400 Bad Request when any field is null")
    void testSaveSurveyBadRequest() throws Exception {

        surveyRequestDtoTest.setTitle(null);
        performPostRequest(surveyRequestDtoTest)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Save Survey: should return 404 Not Found when account or category is invalid")
    void testSaveSurveyNotFound() throws Exception {

        surveyRequestDtoTest.setAccountId(UUID.randomUUID());
        performPostRequest(surveyRequestDtoTest)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get All Surveys: should return 200 OK")
    void getAllSurveys() throws Exception {

        performGetRequest(SURVEY_PATH)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Survey By ID: should return 200 OK")
    void getSurveyById() throws Exception {

        var result = surveyAPI.saveSurvey(surveyRequestDtoTest).getBody().data();
        performGetRequest(SURVEY_PATH_ID, result.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is(result.getTitle())))
                .andExpect(jsonPath("$.data.description", is(result.getDescription())))
                .andExpect(jsonPath("$.data.image", is(result.getImage())));
    }

    @Test
    @DisplayName("Get Survey By ID: should return 404 Not Found when survey ID is invalid")
    void getSurveyByIdNotFound() throws Exception {

        performGetRequest(SURVEY_PATH_ID, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get Survey Questions: should return 200 OK")
    void getSurveyQuestions() throws Exception {

        var result = surveyAPI.saveSurvey(surveyRequestDtoTest).getBody().data();
        performGetRequest(SURVEY_QUESTION_PATH_ID, result.getId())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Survey Questions: should return 404 Not Found when survey ID is invalid")
    void getSurveyQuestionsNotFound() throws Exception {

        performGetRequest(SURVEY_QUESTION_PATH_ID, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update Survey: should return 204 No Content")
    void updateSurvey() throws Exception {

        var result = surveyAPI.saveSurvey(surveyRequestDtoTest).getBody().data();
        performPutRequest(SURVEY_PATH_ID, surveyUpdateRequestDtoTest, result.getId())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Update Survey: should return 404 Not Found when survey ID is invalid")
    void updateSurveyNotFound() throws Exception {

        performPutRequest(SURVEY_PATH_ID, surveyUpdateRequestDtoTest, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete Survey: should return 204 No Content")
    void deleteSurvey() throws Exception {

        var result = surveyAPI.saveSurvey(surveyRequestDtoTest).getBody().data();
        performDeleteRequest(result.getId())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete Survey: should return 404 Not Found when survey ID is invalid")
    void deleteSurveyNotFound() throws Exception {

        performDeleteRequest(UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get accounts: should return all accounts related to a survey")
    void getAccounts() throws Exception {

        var result = surveyAPI.saveSurvey(surveyRequestDtoTest).getBody().data();
        performGetRequest(SURVEY_PATH + "/{surveyId}/accounts", result.getId())
                .andExpect(status().isOk());
    }
}