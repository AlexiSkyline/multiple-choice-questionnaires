package org.skyline.mcq.application.mappings;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.output.CategoryResponseDto;
import org.skyline.mcq.domain.models.Category;

@Mapper
public interface CategoryMapper {

    Category categoryRequestDtoToCategory(CategoryRequestDto categoryRequestDto);
    CategoryResponseDto categoryToCategoryResponseDto(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromCategoryRequestDto(CategoryRequestDto categoryRequestDto, @MappingTarget Category category);
}
