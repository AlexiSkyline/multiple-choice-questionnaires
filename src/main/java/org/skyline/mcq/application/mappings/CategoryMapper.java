package org.skyline.mcq.application.mappings;

import org.mapstruct.Mapper;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.output.CategoryResponseDto;
import org.skyline.mcq.domain.models.Category;

@Mapper
public interface CategoryMapper {

    Category categoryResquestDtoToCategory(CategoryRequestDto categoryRequestDto);
    CategoryResponseDto categoryToCategoryResponseDto(Category category);
}
