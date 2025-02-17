package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Page<Category> findAllByTitleIsLikeIgnoreCase(String title, Pageable pageable);
    Page<Category> findAllByAccount_Id(UUID accountId, Pageable pageable);
}
