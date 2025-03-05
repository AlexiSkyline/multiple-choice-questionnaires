package org.skyline.mcq.application.mappings;

import org.mapstruct.*;
import org.skyline.mcq.application.dtos.input.SurveyRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.domain.models.Survey;

@Mapper
public interface SurveyMapper {

    SurveyResponseDto surveyToSurveyResponseDto(Survey survey);
    Survey surveyRequesttDtoToSurvey(SurveyRequestDto survey);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSurveyFromSurveyUpdateRequestDto(SurveyUpdateRequestDto surveyUpdateRequestDto, @MappingTarget Survey survey);
}
