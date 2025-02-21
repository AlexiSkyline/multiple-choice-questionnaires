package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {}
