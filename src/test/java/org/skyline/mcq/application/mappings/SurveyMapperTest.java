package org.skyline.mcq.application.mappings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.domain.models.Survey;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SurveyMapperTest {

    private final SurveyMapper surveyMapper = Mappers.getMapper(SurveyMapper.class);

    Survey surveyTest;

    @BeforeEach
    void setUp() {

        surveyTest = Survey.builder()
                .id(UUID.randomUUID())
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

    @Test
    @DisplayName("Survey to SurveyResponseDto: Should map survey to survey response DTO correctly")
    void surveyToSurveyResponseDto() {

        var surveyResponseDto = surveyMapper.surveyToSurveyResponseDto(surveyTest);

        assertAll(() -> {
            assertNotNull(surveyResponseDto);
            assertEquals(surveyTest.getTitle(), surveyResponseDto.getTitle());
            assertEquals(surveyTest.getDescription(), surveyResponseDto.getDescription());
            assertEquals(surveyTest.getImage(), surveyResponseDto.getImage());
            assertEquals(surveyTest.getMaxPoints(), surveyResponseDto.getMaxPoints());
            assertEquals(surveyTest.getQuestionCount(), surveyResponseDto.getQuestionCount());
            assertEquals(surveyTest.getTimeLimit(), surveyResponseDto.getTimeLimit());
            assertEquals(surveyTest.getAttempts(), surveyResponseDto.getAttempts());
            assertEquals(surveyTest.getHasRestrictedAccess(), surveyResponseDto.getHasRestrictedAccess());
        });
    }
}