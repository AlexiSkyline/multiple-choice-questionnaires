package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.domain.Question;

import java.util.Optional;
import java.util.UUID;

public interface QuestionInputPort {

    Question saveQuestion(Question question);
    Optional<Question> findQuestionById(UUID id);
    Optional<Question> updateQuestion(UUID uuid, Question question);
    Boolean deleteQuestion(UUID id);
}

