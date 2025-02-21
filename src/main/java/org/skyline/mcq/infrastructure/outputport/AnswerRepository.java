package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    Page<Answer> findAllByResultId(UUID resultId, Pageable pageable);
}
