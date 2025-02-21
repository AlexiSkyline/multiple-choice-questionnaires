package org.skyline.mcq.infrastructure.outputport;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.Survey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Sql(scripts = {"classpath:createSurvey.sql"})
class SurveyRepositoryTest {

    static String idCategory1 = "22222222-2222-2222-2222-222222222222";
    static String idCategory2 = "33333333-3333-3333-3333-333333333333";

    @Autowired
    SurveyRepository surveyRepository;
    Survey newSurvey;

    @BeforeEach
    void setUp() {
        newSurvey = Survey.builder()
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .isActive(true)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        surveyRepository.deleteAll();
    }

    @Test
    @DisplayName("Test saving a survey")
    void testSaveSurvey() {
        var savedSurvey = surveyRepository.save(newSurvey);

        assertAll(
                () -> assertNotNull(savedSurvey.getId()),
                () -> assertEquals(newSurvey.getTitle(), savedSurvey.getTitle())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by category ID")
    void testFindAllSurveyByCategoryId() {
        assertAll(
                () -> assertEquals(0, surveyRepository.findAllByCategoryId(UUID.randomUUID(), Pageable.unpaged()).getContent().size()),
                () -> assertEquals(2, surveyRepository.findAllByCategoryId(UUID.fromString(idCategory1), Pageable.unpaged()).getContent().size()),
                () -> assertEquals(3, surveyRepository.findAllByCategoryId(UUID.fromString(idCategory2), Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by status")
    void testFindAllSurveyByStatus() {
        assertAll(
                () -> assertEquals(2, surveyRepository.findAllByStatus(true, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(3, surveyRepository.findAllByStatus(false, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by category ID and status")
    void testFindAllSurveyByCategoryIdAndStatus() {
        assertAll(
                () -> assertEquals(0, surveyRepository.findAllByCategoryIdAndStatus(UUID.randomUUID(), true, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAllByCategoryIdAndStatus(UUID.fromString(idCategory1), true, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAllByCategoryIdAndStatus(UUID.fromString(idCategory1), false, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAllByCategoryIdAndStatus(UUID.fromString(idCategory2), true, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(2, surveyRepository.findAllByCategoryIdAndStatus(UUID.fromString(idCategory2), false, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by restricted access")
    void testFindAllSurveyByIsPublic() {
        assertAll(
                () -> assertEquals(4, surveyRepository.findAllByHasRestrictedAccess(true, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAllByHasRestrictedAccess(false, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by category ID and restricted access")
    void testFindAllSurveyByCategoryIdAndIsPublic() {
        assertAll(
                () -> assertEquals(0, surveyRepository.findAllByCategoryIdAndHasRestrictedAccess(UUID.randomUUID(), true, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAllByCategoryIdAndHasRestrictedAccess(UUID.fromString(idCategory1), true, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAllByCategoryIdAndHasRestrictedAccess(UUID.fromString(idCategory1), false, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(3, surveyRepository.findAllByCategoryIdAndHasRestrictedAccess(UUID.fromString(idCategory2), true, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(0, surveyRepository.findAllByCategoryIdAndHasRestrictedAccess(UUID.fromString(idCategory2), false, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test updating a survey")
    void testUpdateSurvey() {
        var savedSurvey = surveyRepository.save(newSurvey);
        savedSurvey.setTitle("update title");
        savedSurvey.setDescription("update description");

        var updatedSurvey = surveyRepository.saveAndFlush(savedSurvey);

        assertAll(
                () -> assertNotNull(updatedSurvey.getId()),
                () -> assertEquals("update title", updatedSurvey.getTitle()),
                () -> assertEquals("update description", updatedSurvey.getDescription()),
                () -> assertNotNull(updatedSurvey.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("Test deleting a survey")
    void testDeleteSurvey() {
        var savedSurvey = surveyRepository.save(newSurvey);
        savedSurvey.setActive(false);
        surveyRepository.save(savedSurvey);

        var survey = surveyRepository.findById(savedSurvey.getId())
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        assertFalse(survey.isActive());
    }
}