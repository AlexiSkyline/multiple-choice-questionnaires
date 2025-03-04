package org.skyline.mcq.application.mappings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryMapperTest {

    CategoryMapper categoryMapper =  Mappers.getMapper(CategoryMapper.class);
    Category categoryTest;
    CategoryRequestDto categoryRequestDtoTest;

    @BeforeEach
    void setUp() {

        Account accountTest = Account.builder()
                .id(UUID.randomUUID())
                .firstName("Ethan")
                .lastName("Miller")
                .username("ethan_creator")
                .email("ethan.miller@example.com")
                .password("EthanPassword123")
                .profileImage("creator2.jpg")
                .description("Poll Maker")
                .build();

        categoryTest = Category.builder()
                .id(accountTest.getId())
                .title("New Category")
                .description("New Category")
                .image("New_Category.png")
                .account(accountTest)
                .active(true)
                .build();

        categoryRequestDtoTest = CategoryRequestDto.builder()
                .title("New Category")
                .description("New Category")
                .image("New_Category.png")
                .accountId(accountTest.getId())
                .build();
    }

    @Test
    @DisplayName("CategoryRequestDto to Category: Should map category request DTO to category correctly")
    void categoryRequestDtoToCategory() {

        var result = categoryMapper.categoryRequestDtoToCategory(categoryRequestDtoTest);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(categoryRequestDtoTest.getTitle(), result.getTitle());
            assertEquals(categoryRequestDtoTest.getDescription(), result.getDescription());
            assertEquals(categoryRequestDtoTest.getImage(), result.getImage());
        });
    }

    @Test
    @DisplayName("Category to CategoryResponseDto: Should map category to category response DTO correctly")
    void categoryToCategoryResponseDto() {

        var result = categoryMapper.categoryToCategoryResponseDto(categoryTest);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(categoryTest.getTitle(), result.getTitle());
            assertEquals(categoryTest.getDescription(), result.getDescription());
            assertEquals(categoryTest.getImage(), result.getImage());
        });
    }

    @Test
    @DisplayName("Update Category from CategoryRequestDto: Should update category fields from category request DTO")
    void updateCategoryFromCategoryRequestDto() {

        var categoryRequestDto = CategoryRequestDto.builder()
                .title("Update Category")
                .description("Update Category")
                .image("Update_Category.png")
                .accountId(UUID.randomUUID())
                .build();

        categoryMapper.updateCategoryFromCategoryRequestDto(categoryRequestDto, categoryTest);

        assertAll(() -> {
            assertEquals(categoryRequestDto.getTitle(), categoryTest.getTitle());
            assertEquals(categoryRequestDto.getDescription(), categoryTest.getDescription());
            assertEquals(categoryRequestDto.getImage(), categoryTest.getImage());
        });
    }
}