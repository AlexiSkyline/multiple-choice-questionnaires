package org.skyline.mcq.application.mappings;

import org.mapstruct.Mapper;
import org.skyline.mcq.application.dtos.output.ResultResponseDto;
import org.skyline.mcq.domain.models.Result;

@Mapper(uses = {SurveyMapper.class, AnswerMapper.class, AnswerMapper.class})
public interface ResultMapper {

    ResultResponseDto resultToResultResponseDto(Result result);
}
