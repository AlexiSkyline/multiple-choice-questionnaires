package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.domain.models.Answer;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AnswerInputPort {

    Answer saveAnswer(Answer answer);
    Page<Answer> listAnswerByResultId(UUID resultId, Integer pageNumber, Integer pageSize);
}
