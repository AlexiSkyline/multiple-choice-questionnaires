package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.models.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResultRepository extends JpaRepository<Result, UUID> {

    Optional<Result> findByIdAndAccountId(UUID id, UUID accountId);
    Page<Result> findAllByAccountIdAndSurveyId(UUID accountId, UUID surveyId, Pageable pageable);
    Page<Result> findAllByAccountId(UUID accountId, Pageable pageable);
    Page<Result> findAllBySurveyId(UUID surveyId, Pageable pageable);
}
