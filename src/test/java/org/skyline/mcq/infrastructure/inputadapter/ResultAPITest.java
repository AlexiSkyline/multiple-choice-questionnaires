package org.skyline.mcq.infrastructure.inputadapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Result;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.ResultRepository;
import org.skyline.mcq.infrastructure.outputport.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ResultAPITest {

    private static final String RESULT_PATH = "/api/v1/results";
    private static final String RESULT_PATH_ID = "/api/v1/results/{resultId}";
    private static final String RESULT_PATH_SURVEY_ID = RESULT_PATH + "/survey/{surveyId}";
    private static final String RESULT_PATH_ACCOUNT_ID = RESULT_PATH + "/account/{accountId}";
    private static final String RESULT_PATH_SURVEY_ID_ACCOUNT_ID = RESULT_PATH + "/survey/{surveyId}/account/{accountId}";

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private Result resultTest;
    private Survey surveyTest;
    private Account accountTest;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        resultTest = Result.builder()
                .startTime(Timestamp.valueOf(LocalDateTime.now()))
                .endTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)))
                .duration(1800)
                .totalPoints(20)
                .correctAnswers(10)
                .incorrectAnswers(10)
                .build();
        surveyTest = Survey.builder()
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .active(true)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .build();
        accountTest = Account.builder()
                .firstName("new account")
                .lastName("new account")
                .username("new_ac_responder")
                .email("new.account@example.com")
                .password("NewPassword123")
                .profileImage("new.account1.jpg")
                .description("New Account responder")
                .build();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Get Result by ID: Should return 200 OK")
    void testGetResultById() throws Exception {

        var result = resultRepository.save(resultTest);
        mockMvc.perform(get(RESULT_PATH_ID, result.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Result by ID: Should return 404 Not Found")
    void testGetResultByIdNotFound() throws Exception {

        mockMvc.perform(get(RESULT_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Get Result by Survey ID: Should return 200 OK")
    void testGetResultBySurveyId() throws Exception {

        var result = surveyRepository.save(surveyTest);
        mockMvc.perform(get(RESULT_PATH_SURVEY_ID, result.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Result by Survey ID: Should return 404 Not Found")
    void testGetResultBySurveyIdNotFound() throws Exception {

        mockMvc.perform(get(RESULT_PATH_SURVEY_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Get Result by Account ID: Should return 200 OK")
    void testGetResultByAccountId() throws Exception {

        var result = accountRepository.save(accountTest);
        mockMvc.perform(get(RESULT_PATH_ACCOUNT_ID, result.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Result by Account ID: Should return 404 Not Found")
    void testGetResultByAccountIdNotFound() throws Exception {

        mockMvc.perform(get(RESULT_PATH_ACCOUNT_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Get Result by Survey ID and Account ID: Should return 200 OK")
    void testGetResultBySurveyIdAndAccountId() throws Exception {

        var survey = surveyRepository.save(surveyTest);
        var result = accountRepository.save(accountTest);

        mockMvc.perform(get(RESULT_PATH_SURVEY_ID_ACCOUNT_ID, survey.getId(), result.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Get Result by Survey ID and Account ID: Should return 404 Not Found when survey does not exist")
    void testGetResultBySurveyIdAndAccountIdNotFoundSurvey() throws Exception {

        var result = accountRepository.save(accountTest);
        mockMvc.perform(get(RESULT_PATH_SURVEY_ID_ACCOUNT_ID, UUID.randomUUID(), result.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Get Result by Survey ID and Account ID: Should return 404 Not Found when account does not exist")
    void testGetResultBySurveyIdAndAccountIdNotFoundAccount() throws Exception {

        var survey = surveyRepository.save(surveyTest);
        mockMvc.perform(get(RESULT_PATH_SURVEY_ID_ACCOUNT_ID, survey.getId(), UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}