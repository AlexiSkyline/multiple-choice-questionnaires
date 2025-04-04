package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.output.CategoryResponseDto;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface CategoryInputPort {

    Optional<CategoryResponseDto> saveCategory(UUID accountId, CategoryRequestDto category);
    Page<CategoryResponseDto> listCategories(UUID accountId, String title, Boolean isActive, Integer pageNumber, Integer pageSize);
    Optional<CategoryResponseDto> updateCategory(UUID id, UUID accountId, CategoryRequestDto category);
    Boolean deleteCategoryByIdAndByAccountId(UUID id, UUID accountId);
}
