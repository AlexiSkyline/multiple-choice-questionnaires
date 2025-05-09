package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    Optional<Question> findByIdAndSurveyAccountId(UUID uuid, UUID accountId);
}
