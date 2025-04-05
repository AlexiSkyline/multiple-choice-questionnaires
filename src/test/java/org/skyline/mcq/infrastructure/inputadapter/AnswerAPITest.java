package org.skyline.mcq.infrastructure.inputadapter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.SignUpRequestDto;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.domain.models.Result;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.infrastructure.inputport.ResultInputPort;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnswerAPITest {


    @Autowired
    private MockMvc mockMvc;

    private static final String ANSWER_RESULT_ID_PATH = "/api/v1/answers/{resultId}";

    private static String tokenRespondent;
    private static UUID resultId;

    @BeforeAll
    static void initializeTestEnvironment(@Autowired AuthAPI authAPI, @Autowired AccountRepository accountRepository,
                                          @Autowired ResultInputPort resultInputPort, @Autowired SurveyRepository surveyRepository,
                                          @Autowired CategoryRepository categoryRepository) {

        tokenRespondent = createAndAuthenticateUser(authAPI);

        Category category = categoryRepository.findAll().getFirst();
        accountRepository.findByEmail("respondent.userTest.answer.test@gmail.com").ifPresent(account -> {
            resultId = createTestResult(resultInputPort, account, createTestSurvey(surveyRepository, account, category));
        });
    }

    private static Survey createTestSurvey(SurveyRepository repository, Account account, Category category) {
        Survey request = Survey.builder()
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .category(category)
                .account(account)
                .status(true)
                .build();

        return repository.save(request);
    }

    private static UUID createTestResult(ResultInputPort resultInputPort, Account account, Survey survey) {
        Result resultTest = Result.builder()
                .startTime(Timestamp.valueOf(LocalDateTime.now()))
                .endTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)))
                .duration(1800)
                .totalPoints(20)
                .correctAnswers(10)
                .incorrectAnswers(10)
                .account(account)
                .survey(survey)
                .build();

        return resultInputPort.saveResult(resultTest).getId();
    }

    private static String createAndAuthenticateUser(AuthAPI authAPI) {
        SignUpRequestDto request = SignUpRequestDto.builder()
                .firstName("Respondent answer UserTest")
                .lastName("lastname answer respondent".toLowerCase())
                .username("RespondentAnsTest")
                .email("respondent.userTest.answer.test@gmail.com")
                .password("password")
                .build();

        return Objects.requireNonNull(authAPI.registerUserWithRole(request, TypeRole.ROLE_SURVEY_RESPONDENT).getBody()).getAccessToken();
    }

    private ResultActions performAuthorizedRequest(String token, Object... uriVars) throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(HttpMethod.GET, AnswerAPITest.ANSWER_RESULT_ID_PATH, uriVars)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        return mockMvc.perform(request);
    }


    @Test
    @Rollback
    @Transactional
    @DisplayName("List answers by result id: Should return 200 OK")
    void listAnswerByResultId() throws Exception {

        performAuthorizedRequest(tokenRespondent, resultId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.data.content").isArray()
                );
    }

    @Test
    @DisplayName("List answers by result id: Should return 404 Not Found")
    void listAnswerByResultIdNotFound() throws Exception {

        performAuthorizedRequest(tokenRespondent, UUID.randomUUID())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404)
                );
    }
}