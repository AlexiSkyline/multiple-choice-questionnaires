package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.output.CategoryResponseDto;
import org.skyline.mcq.domain.models.Category;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface CategoryInputPort {

    CategoryResponseDto saveCategory(Category category);
    Page<CategoryResponseDto> listCategories(UUID accountId, String title, Boolean isActive, Integer pageNumber, Integer pageSize);
    Optional<CategoryResponseDto> updateCategory(UUID id, CategoryRequestDto category);
    Boolean deleteCategory(UUID id);
}
