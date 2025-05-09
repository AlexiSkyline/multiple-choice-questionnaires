package org.skyline.mcq.infrastructure.outputport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Question;
import org.skyline.mcq.domain.models.Survey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = {"classpath:createSurvey.sql"})
class QuestionRepositoryTest {

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    QuestionRepository questionRepository;

    Question questionTest;
    Survey surveyTest;
    Account accountTest;

    @BeforeEach
    void setUp() {
        surveyTest = surveyRepository.findAll().getFirst();
        accountTest = surveyTest.getAccount();

        questionTest = Question.builder()
                .content("Example new Question")
                .image("question.jpg")
                .points(5)
                .allowedAnswers(2)
                .options("{a, b, c, d, e}")
                .correctAnswers("{b, d}")
                .survey(surveyTest)
                .build();
    }

    @AfterEach
    void tearDown() {
        questionRepository.deleteAll();
    }

    @Test
    @DisplayName("Test saving a question")
    void testSaveQuestion() {
        var savedQuestion = questionRepository.save(questionTest);

        assertAll(
                () -> assertNotNull(savedQuestion.getId()),
                () -> assertEquals(questionTest.getContent(), savedQuestion.getContent()),
                () -> assertEquals(questionTest.getImage(), savedQuestion.getImage())
        );
    }

    @Test
    @Rollback
    @DisplayName("Test finding a question by ID")
    void testFindById() {
        var savedQuestion = questionRepository.save(questionTest);

        var questionFound = questionRepository.findById(savedQuestion.getId());

        assertAll(
                () -> assertTrue(questionFound.isPresent()),
                () -> assertFalse(questionFound.isEmpty())
        );
    }

    @Test
    @DisplayName("Test finding a question by ID when it does not exist")
    void testFindByIdNotFound() {
        var questionFound = questionRepository.findById(UUID.randomUUID());

        assertTrue(questionFound.isEmpty());
    }

    @Test
    @Rollback
    @DisplayName("Test finding a question by ID and AccountId")
    void testFindByIdAndAccountId() {
        var savedQuestion = questionRepository.save(questionTest);

        var questionFound = questionRepository.findByIdAndSurveyAccountId(savedQuestion.getId(), accountTest.getId());

        assertAll(
                () -> assertTrue(questionFound.isPresent()),
                () -> assertFalse(questionFound.isEmpty())
        );
    }

    @Test
    @Rollback
    @DisplayName("Test finding a question by ID and AccountId")
    void testFindByIdAndAccountIdNotFound() {
        var savedQuestion = questionRepository.save(questionTest);

        var questionFound = questionRepository.findByIdAndSurveyAccountId(savedQuestion.getId(), UUID.randomUUID());

        assertTrue(questionFound.isEmpty());
    }

    @Test
    @DisplayName("Test updating a question")
    void testUpdateQuestion() {
        var savedQuestion = questionRepository.save(questionTest);

        savedQuestion.setImage("update_image.jpg");
        savedQuestion.setContent("update content");
        var updatedQuestion = questionRepository.saveAndFlush(savedQuestion);

        assertAll(
                () -> assertEquals("update_image.jpg", updatedQuestion.getImage()),
                () -> assertEquals("update content", updatedQuestion.getContent()),
                () -> assertNotNull(updatedQuestion.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("Test deleting a question")
    void testDeleteQuestion() {
        var savedQuestion = questionRepository.save(questionTest);

        questionRepository.delete(savedQuestion);

        assertTrue(questionRepository.findById(savedQuestion.getId()).isEmpty());
    }
}