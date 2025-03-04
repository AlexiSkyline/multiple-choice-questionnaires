package org.skyline.mcq.application.mappings;

import org.mapstruct.Mapper;
import org.skyline.mcq.application.dtos.output.AnswerResponseDto;
import org.skyline.mcq.domain.models.Answer;

@Mapper(uses = {QuestionMapper.class, AccountMapper.class})
public interface AnswerMapper {

    AnswerResponseDto answerToAnswerResponseDto(Answer answer);
}
