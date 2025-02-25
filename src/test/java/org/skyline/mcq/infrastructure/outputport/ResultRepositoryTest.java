package org.skyline.mcq.infrastructure.outputport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Result;
import org.skyline.mcq.domain.models.Survey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = {"classpath:createSurvey.sql"})
class ResultRepositoryTest {

    static String accountId = "22222222-2222-2222-2222-222222222222";
    static String surveyId = "55555555-5555-5555-5555-555555555555";

    @Autowired
    ResultRepository resultRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SurveyRepository surveyRepository;

    Account accountTest;
    Result resultTest;
    Survey surveyTest;

    @BeforeEach
    void setUp() {
        accountTest = accountRepository.findById(UUID.fromString(accountId)).orElseThrow(() -> new RuntimeException("Account not found"));
        surveyTest = surveyRepository.findById(UUID.fromString(surveyId)).orElseThrow(() -> new RuntimeException("Survey not found"));

        resultTest = Result.builder()
                .account(accountTest)
                .survey(surveyTest)
                .startTime(Timestamp.valueOf(LocalDateTime.now()))
                .endTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)))
                .duration(1800)
                .totalPoints(20)
                .correctAnswers(10)
                .incorrectAnswers(10)
                .build();
    }

    @AfterEach
    void tearDown() {
        resultRepository.deleteAll();
    }

    @Test
    @DisplayName("Test saving a result")
    void testSaveResult() {
        var savedResult = resultRepository.save(resultTest);

        assertAll(() -> {
            assertNotNull(savedResult);
            assertNotNull(savedResult.getId());
            assertEquals(resultTest.getAccount().getId(), savedResult.getAccount().getId());
            assertEquals(resultTest.getSurvey().getId(), savedResult.getSurvey().getId());
            assertEquals(resultTest.getTotalPoints(), savedResult.getTotalPoints());
            assertEquals(resultTest.getCorrectAnswers(), savedResult.getCorrectAnswers());
            assertEquals(resultTest.getIncorrectAnswers(), savedResult.getIncorrectAnswers());
        });
    }

    @Test
    @DisplayName("Test finding results by account ID and survey ID")
    void testFindAllByAccountIdAndSurveyId() {
        resultRepository.save(resultTest);

        var resultsFound = resultRepository.findAllByAccountIdAndSurveyId(
                UUID.fromString(accountId), UUID.fromString(surveyId), Pageable.unpaged()).getContent();

        assertAll(() -> {
            assertNotNull(resultsFound);
            assertFalse(resultsFound.isEmpty());
            assertEquals(2, resultsFound.size());
            assertEquals(accountId, resultsFound.getFirst().getAccount().getId().toString());
            assertEquals(surveyId, resultsFound.getFirst().getSurvey().getId().toString());
        });
    }

    @Test
    @DisplayName("Test finding results by account ID and survey ID when no results exist")
    void testFindAllByAccountIdAndSurveyIdNotFound() {
        var resultsFound = resultRepository.findAllByAccountIdAndSurveyId(
                UUID.randomUUID(), UUID.randomUUID(), Pageable.unpaged()).getContent();

        assertAll(() -> {
            assertTrue(resultsFound.isEmpty());
            assertEquals(0, resultsFound.size());
        });
    }

    @Test
    @DisplayName("Test finding results by account ID")
    void testFindAllByAccountId() {
        resultRepository.save(resultTest);

        var resultsFound = resultRepository.findAllByAccountId(
                UUID.fromString(accountId), Pageable.unpaged()).getContent();

        assertAll(() -> {
            assertNotNull(resultsFound);
            assertFalse(resultsFound.isEmpty());
            assertEquals(3, resultsFound.size());
            assertEquals(accountId, resultsFound.getFirst().getAccount().getId().toString());
        });
    }

    @Test
    @DisplayName("Test finding results by account ID when no results exist")
    void testFindAllByAccountIdNotFound() {
        var resultsFound = resultRepository.findAllByAccountId(
                UUID.randomUUID(), Pageable.unpaged()).getContent();

        assertAll(() -> {
            assertTrue(resultsFound.isEmpty());
            assertEquals(0, resultsFound.size());
        });
    }

    @Test
    @DisplayName("Test finding results by survey ID")
    void testFindAllBySurveyId() {
        resultRepository.save(resultTest);

        var resultsFound = resultRepository.findAllBySurveyId(UUID.fromString(surveyId), Pageable.unpaged()).getContent();

        assertAll(() -> {
            assertNotNull(resultsFound);
            assertFalse(resultsFound.isEmpty());
            assertEquals(2, resultsFound.size());
            assertEquals(surveyId, resultsFound.getFirst().getSurvey().getId().toString());
        });
    }
}