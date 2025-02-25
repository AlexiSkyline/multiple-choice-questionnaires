package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Page<Category> findAllByActive(Boolean isActive, Pageable pageable);
    Page<Category> findAllByTitleIsLikeIgnoreCaseAndActive(String title, Boolean isActive, Pageable pageable);
    Page<Category> findAllByAccountIdAndActiveIsTrue(UUID accountId, Pageable pageable);
}
