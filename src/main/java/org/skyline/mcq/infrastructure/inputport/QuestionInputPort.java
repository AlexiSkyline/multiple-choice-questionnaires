package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.input.QuestionRequestDto;
import org.skyline.mcq.application.dtos.input.QuestionUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.QuestionResponseDto;

import java.util.Optional;
import java.util.UUID;

public interface QuestionInputPort {

    Optional<QuestionResponseDto> saveQuestion(QuestionRequestDto question);
    Optional<QuestionResponseDto> findQuestionById(UUID id);
    Optional<QuestionResponseDto> findQuestionByIdAndAccountId(UUID id, UUID accountId);
    Optional<QuestionResponseDto> updateQuestion(UUID uuid, QuestionUpdateRequestDto question);
    Boolean deleteQuestion(UUID id, UUID accountId);
}
