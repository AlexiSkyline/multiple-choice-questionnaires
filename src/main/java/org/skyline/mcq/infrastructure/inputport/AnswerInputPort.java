package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.output.AnswerResponseDto;
import org.skyline.mcq.domain.models.Answer;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AnswerInputPort {

    AnswerResponseDto saveAnswer(Answer answer);
    Page<AnswerResponseDto> listAnswerByResultId(UUID resultId, Integer pageNumber, Integer pageSize);
}
