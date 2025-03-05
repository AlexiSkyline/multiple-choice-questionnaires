package org.skyline.mcq.application.mappings;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.skyline.mcq.application.dtos.input.QuestionRequestDto;
import org.skyline.mcq.application.dtos.input.QuestionUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.QuestionResponseDto;
import org.skyline.mcq.domain.models.Question;

@Mapper
public interface QuestionMapper {

    QuestionResponseDto questionToQuestionResponseDto(Question question);
    Question questionRequestDtoToQuestion(QuestionRequestDto questionRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateQuestionFromQuestionRequestDto(QuestionUpdateRequestDto questionUpdateRequestDto, @MappingTarget Question question);
}
