package org.skyline.mcq.infrastructure.outputport;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.domain.specification.SurveySpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
                .active(true)
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

        Specification<Survey> specRandomId = SurveySpecifications.hasCategoryId(UUID.randomUUID());
        Specification<Survey> specIdCategory1 = SurveySpecifications.hasCategoryId(UUID.fromString(idCategory1));
        Specification<Survey> specIdCategory2 = SurveySpecifications.hasCategoryId(UUID.fromString(idCategory2));

        assertAll(
                () -> assertEquals(0, surveyRepository.findAll(specRandomId, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(2, surveyRepository.findAll(specIdCategory1, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(3, surveyRepository.findAll(specIdCategory2, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by active")
    void testFindAllSurveyByActive() {

        Specification<Survey> specStatusTrue = SurveySpecifications.hasActive(true);
        Specification<Survey> specStatusFalse = SurveySpecifications.hasActive(false);

        assertAll(
                () -> assertEquals(5, surveyRepository.findAll(specStatusTrue, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(0, surveyRepository.findAll(specStatusFalse, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by status")
    void testFindAllSurveyByStatus() {

        Specification<Survey> specStatusTrue = SurveySpecifications.hasStatus(true);
        Specification<Survey> specStatusFalse = SurveySpecifications.hasStatus(false);

        assertAll(
                () -> assertEquals(2, surveyRepository.findAll(specStatusTrue, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(3, surveyRepository.findAll(specStatusFalse, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by category ID and status")
    void testFindAllSurveyByCategoryIdAndStatus() {

        Specification<Survey> specRandomId = SurveySpecifications.hasCategoryIdAndStatus(UUID.randomUUID(), true);
        Specification<Survey> specIdCategory1True = SurveySpecifications.hasCategoryIdAndStatus(UUID.fromString(idCategory1), true);
        Specification<Survey> specIdCategory1False = SurveySpecifications.hasCategoryIdAndStatus(UUID.fromString(idCategory1), false);
        Specification<Survey> specIdCategory2True = SurveySpecifications.hasCategoryIdAndStatus(UUID.fromString(idCategory2), true);
        Specification<Survey> specIdCategory2False = SurveySpecifications.hasCategoryIdAndStatus(UUID.fromString(idCategory2), false);

        assertAll(
                () -> assertEquals(0, surveyRepository.findAll(specRandomId, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAll(specIdCategory1True, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAll(specIdCategory1False, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAll(specIdCategory2True, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(2, surveyRepository.findAll(specIdCategory2False, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by restricted access")
    void testFindAllSurveyByIsPublic() {

        Specification<Survey> specAccessTrue = SurveySpecifications.hasRestrictedAccess(true);
        Specification<Survey> specAccessFalse = SurveySpecifications.hasRestrictedAccess(false);

        assertAll(
                () -> assertEquals(4, surveyRepository.findAll(specAccessTrue, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAll(specAccessFalse, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by category ID and restricted access")
    void testFindAllSurveyByCategoryIdAndIsPublic() {

        Specification<Survey> specRandomId = SurveySpecifications.hasCategoryIdAndHasRestrictedAccess(UUID.randomUUID(), true);
        Specification<Survey> specIdCategory1True = SurveySpecifications.hasCategoryIdAndHasRestrictedAccess(UUID.fromString(idCategory1), true);
        Specification<Survey> specIdCategory1False = SurveySpecifications.hasCategoryIdAndHasRestrictedAccess(UUID.fromString(idCategory1), false);
        Specification<Survey> specIdCategory2True = SurveySpecifications.hasCategoryIdAndHasRestrictedAccess(UUID.fromString(idCategory2), true);
        Specification<Survey> specIdCategory2False = SurveySpecifications.hasCategoryIdAndHasRestrictedAccess(UUID.fromString(idCategory2), false);

        assertAll(
                () -> assertEquals(0, surveyRepository.findAll(specRandomId, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAll(specIdCategory1True, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(1, surveyRepository.findAll(specIdCategory1False, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(3, surveyRepository.findAll(specIdCategory2True, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(0, surveyRepository.findAll(specIdCategory2False, Pageable.unpaged()).getContent().size())
        );
    }

    @Test
    @DisplayName("Test finding all surveys by category ID and active")
    void testFindAllSurveyByCategoryIdAndActive() {

        String accountId = "11111111-1111-1111-1111-111111111111";
        Specification<Survey> specRandomId = SurveySpecifications.hasAccountIdAndIsActive(UUID.randomUUID(), true);
        Specification<Survey> specIdCategory1True = SurveySpecifications.hasAccountIdAndIsActive(UUID.fromString(accountId), true);
        Specification<Survey> specIdCategory1False = SurveySpecifications.hasAccountIdAndIsActive(UUID.fromString(accountId), false);

        assertAll(
                () -> assertEquals(0, surveyRepository.findAll(specRandomId, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(2, surveyRepository.findAll(specIdCategory1True, Pageable.unpaged()).getContent().size()),
                () -> assertEquals(3, surveyRepository.findAll(specIdCategory1False, Pageable.unpaged()).getContent().size())
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
        assertFalse(survey.getActive());
    }

    @Test
    @DisplayName("Test finding all accounts by survey ID")
    void testListAccountsBySurveyId() {

        var surveyId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        var result = surveyRepository.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyId, true, true, Pageable.unpaged());

        assertAll(() -> {
            assertFalse(result.isEmpty());
            assertEquals(1, result.getContent().size());
        });
    }
}