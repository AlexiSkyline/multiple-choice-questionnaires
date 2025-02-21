package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SurveyRepository extends JpaRepository<Survey, UUID> {

    Page<Survey> findAllByCategoryId(UUID id, Pageable pageble);
    Page<Survey> findAllByStatus(Boolean status, Pageable pageable);
    Page<Survey> findAllByCategoryIdAndStatus(UUID categoryId, Boolean status, Pageable pageable);
    Page<Survey> findAllByHasRestrictedAccess(Boolean isPublic, Pageable pageable);
    Page<Survey> findAllByCategoryIdAndHasRestrictedAccess(UUID categoryId, Boolean isPublic, Pageable pageable);
}
