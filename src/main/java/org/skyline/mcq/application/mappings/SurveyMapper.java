package org.skyline.mcq.application.mappings;

import org.mapstruct.Mapper;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.domain.models.Survey;

@Mapper
public interface SurveyMapper {

    SurveyResponseDto surveyToSurveyResponseDto(Survey survey);
}
