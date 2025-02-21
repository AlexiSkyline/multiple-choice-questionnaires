package org.skyline.mcq.infrastructure.outputport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.Account;
import org.skyline.mcq.domain.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = {"classpath:createSurvey.sql"})
class AnswerRepositoryTest {

    static String accountId = "22222222-2222-2222-2222-222222222222";

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    ResultRepository resultRepository;

    @Autowired
    AccountRepository accountRepository;

    Answer answerTest;
    Account accountTest;

    @BeforeEach
    void setUp() {
        accountTest = accountRepository.findById(UUID.fromString(accountId)).get();

        answerTest = Answer.builder()
                .account(accountTest)
                .userAnswers("{a, b, c}")
                .isCorrect(false)
                .points(2)
                .build();
    }

    @AfterEach
    void tearDown() {
        answerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test saving an answer")
    void testSaveAnswer() {

        var savedAnswer = answerRepository.save(answerTest);

        assertAll(
                () -> assertNotNull(savedAnswer),
                () -> assertNotNull(savedAnswer.getId()),
                () -> assertEquals(answerTest.getUserAnswers(), savedAnswer.getUserAnswers()),
                () -> assertEquals(answerTest.getPoints(), savedAnswer.getPoints()),
                () -> assertEquals(answerTest.getIsCorrect(), savedAnswer.getIsCorrect())
        );
    }

    @Test
    @DisplayName("Test finding answers by result ID")
    void testFindByResultId() {
        var resultFound = resultRepository.findAll().getFirst();
        assertNotNull(resultFound);

        answerTest.setResult(resultFound);
        answerRepository.save(answerTest);

        var answersFound = answerRepository.findAllByResultId(resultFound.getId(), Pageable.unpaged());

        assertAll(
                () -> assertFalse(answersFound.isEmpty()),
                () -> assertEquals(4, answersFound.getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding answers by result ID when no answers exist")
    void testFindByResultIdNotFound() {
        var answersFound = answerRepository.findAllByResultId(UUID.randomUUID(), Pageable.unpaged());

        assertTrue(answersFound.isEmpty());
    }
}