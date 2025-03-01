package org.skyline.mcq.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.output.CategoryResponseDto;
import org.skyline.mcq.application.mappings.CategoryMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private PaginationHelper paginationHelper;

    @InjectMocks
    private CategoryService categoryService;

    private Account accountTest;
    private Category categoryTest;
    private CategoryResponseDto categoryResponseDtoTest;
    private CategoryRequestDto categoryRequestDtoTest;
    private Page<Category> categoryPage;
    private UUID accountId;
    private PageRequest pageable;
    private String title;

    @BeforeEach
    void setUp() {

        accountTest = Account.builder()
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
                .id(UUID.randomUUID())
                .title("New Category")
                .description("New Category")
                .image("New_Category.png")
                .active(true)
                .build();

        categoryResponseDtoTest = CategoryResponseDto.builder()
                .title("New Category")
                .description("New Category")
                .image("New_Category.png")
                .build();

        categoryRequestDtoTest = CategoryRequestDto.builder()
                .title("New Category")
                .description("New Category")
                .image("New_Category.png")
                .accountId(accountTest.getId())
                .build();

        List<Category> categories = List.of(
                Category.builder()
                        .id(UUID.randomUUID())
                        .title("Category 1")
                        .description("Description 1")
                        .image("image1.png")
                        .active(true)
                        .build(),
                Category.builder()
                        .id(UUID.randomUUID())
                        .title("Category 2")
                        .description("Description 2")
                        .image("image2.png")
                        .active(true)
                        .build()
        );

        title = "Category";
        accountId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);
        categoryPage = new PageImpl<>(categories, pageable, categories.size());
    }

    @Test
    @DisplayName("Save Category: Should persist and map category correctly")
    void testSaveCategory() {

        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.of(accountTest));
        given(categoryMapper.categoryToCategoryResponseDto(categoryTest)).willReturn(categoryResponseDtoTest);
        given(categoryRepository.save(categoryTest)).willReturn(categoryTest);
        given(categoryMapper.categoryResquestDtoToCategory(categoryRequestDtoTest)).willReturn(categoryTest);

        Optional<CategoryResponseDto> result = categoryService.saveCategory(categoryRequestDtoTest);

        assertAll(() -> {
            assertNotNull(result);
            assertTrue(result.isPresent());
        });

        verify(accountRepository).findById(accountTest.getId());
        verify(categoryRepository, times(1)).save(categoryTest);
        verify(categoryMapper, times(1)).categoryToCategoryResponseDto(categoryTest);
        verify(categoryMapper).categoryResquestDtoToCategory(categoryRequestDtoTest);
    }

    @Test
    @DisplayName("Save Category: Should return empty when account is not found")
    void testSaveCategoryAccountNotFound() {

        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.empty());

        Optional<CategoryResponseDto> result = categoryService.saveCategory(categoryRequestDtoTest);

        assertAll(() -> {
            assertNotNull(result);
            assertFalse(result.isPresent());
        });

        verify(accountRepository).findById(accountTest.getId());
        verify(categoryRepository, never()).save(categoryTest);
        verify(categoryMapper, never()).categoryToCategoryResponseDto(categoryTest);
        verify(categoryMapper, never()).categoryResquestDtoToCategory(categoryRequestDtoTest);
    }


    @Test
    @DisplayName("List Categories by Account ID: Should return a page of categories by account ID")
    void testListCategoriesByAccountId() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(categoryRepository.findAllByAccountIdAndActiveIsTrue(accountId, pageable)).willReturn(categoryPage);
        given(categoryMapper.categoryToCategoryResponseDto(any(Category.class))).willReturn(categoryResponseDtoTest);

        Page<CategoryResponseDto> result = categoryService.listCategories(accountId, null, null, 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
        });

        verify(categoryRepository, times(1)).findAllByAccountIdAndActiveIsTrue(accountId, pageable);
        verify(categoryMapper, times(2)).categoryToCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("List Categories by Title and Active: Should return a page of categories by title and active")
    void testListCategoriesByTitleActive() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(categoryRepository.findAllByTitleIsLikeIgnoreCaseAndActive("%" + title + "%", true, pageable)).willReturn(categoryPage);
        given(categoryMapper.categoryToCategoryResponseDto(any(Category.class))).willReturn(categoryResponseDtoTest);

        Page<CategoryResponseDto> result = categoryService.listCategories(null, title, true, 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
        });

        verify(categoryRepository, times(1)).findAllByTitleIsLikeIgnoreCaseAndActive("%" + title + "%", true, pageable);
        verify(categoryMapper, times(2)).categoryToCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("List Categories by Title and Inactive: Should return a page of categories by title and inactive")
    void testListCategoriesByTitleInactive() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(categoryRepository.findAllByTitleIsLikeIgnoreCaseAndActive("%" + title + "%", false, pageable)).willReturn(categoryPage);
        given(categoryMapper.categoryToCategoryResponseDto(any(Category.class))).willReturn(categoryResponseDtoTest);

        Page<CategoryResponseDto> result = categoryService.listCategories(null, title, false, 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
        });

        verify(categoryRepository, times(1)).findAllByTitleIsLikeIgnoreCaseAndActive("%" + title + "%", false, pageable);
        verify(categoryMapper, times(2)).categoryToCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("List Categories by Empty Title and Active: Should return an empty page when title is empty and isActive is true")
    void testListCategoriesByEmptyTitleActive() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);

        Page<CategoryResponseDto> result = categoryService.listCategories(null, "", true, 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(0, result.getContent().size());
        });

        verify(categoryRepository, never()).findAllByTitleIsLikeIgnoreCaseAndActive("%%", true, pageable);
        verify(categoryMapper, never()).categoryToCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("List Categories by Title and Null Active: Should return an empty page when title is provided but isActive is null")
    void testListCategoriesByTitleActiveFailed() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);

        Page<CategoryResponseDto> result = categoryService.listCategories(null, "category", null, 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(0, result.getContent().size());
        });

        verify(categoryRepository, never()).findAllByTitleIsLikeIgnoreCaseAndActive("%category%", null, pageable);
        verify(categoryMapper, never()).categoryToCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("List Active Categories: Should return a page of active categories")
    void testListCategoriesActives() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(categoryRepository.findAllByActive(true, pageable)).willReturn(categoryPage);
        given(categoryMapper.categoryToCategoryResponseDto(any(Category.class))).willReturn(categoryResponseDtoTest);

        Page<CategoryResponseDto> result = categoryService.listCategories(null, null, true, 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
        });

        verify(categoryRepository, times(1)).findAllByActive(true, pageable);
        verify(categoryMapper, times(2)).categoryToCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("List Inactive Categories: Should return a page of inactive categories")
    void testListCategoriesInactive() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(categoryRepository.findAllByActive(false, pageable)).willReturn(categoryPage);
        given(categoryMapper.categoryToCategoryResponseDto(any(Category.class))).willReturn(categoryResponseDtoTest);

        Page<CategoryResponseDto> result = categoryService.listCategories(null, null, false, 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
        });

        verify(categoryRepository, times(1)).findAllByActive(false, pageable);
        verify(categoryMapper, times(2)).categoryToCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("List Categories with Null Filters: Should return an empty page when no filters are provided")
    void testListCategories() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);

        Page<CategoryResponseDto> result = categoryService.listCategories(null, null, null, 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(0, result.getContent().size());
        });

        verify(categoryRepository, never()).findAllByActive(false, pageable);
        verify(categoryMapper, never()).categoryToCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("List Categories by Account ID: Should return an empty page when no categories are found for the account")
    void testListCategoriesEmpty() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(categoryRepository.findAllByAccountIdAndActiveIsTrue(accountId, pageable)).willReturn(Page.empty());

        Page<CategoryResponseDto> result = categoryService.listCategories(accountId, null, null, 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(0, result.getContent().size());
        });

        verify(categoryRepository, times(1)).findAllByAccountIdAndActiveIsTrue(accountId, pageable);
        verify(categoryMapper, never()).categoryToCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("Update Category: Should update category when it exists and is active")
    void testUpdateCategory() {

        given(categoryRepository.findById(categoryTest.getId())).willReturn(Optional.of(categoryTest));
        given(categoryRepository.save(categoryTest)).willReturn(categoryTest);
        given(categoryMapper.categoryToCategoryResponseDto(categoryTest)).willReturn(categoryResponseDtoTest);

        Optional<CategoryResponseDto> result = categoryService.updateCategory(categoryTest.getId(), categoryRequestDtoTest);

        assertAll(() -> {
            assertTrue(result.isPresent());
            assertEquals(categoryResponseDtoTest, result.get());
        });

        verify(categoryRepository, times(1)).findById(categoryTest.getId());
        verify(categoryRepository, times(1)).save(categoryTest);
        verify(categoryMapper, times(1)).categoryToCategoryResponseDto(categoryTest);
    }

    @Test
    @DisplayName("Update Category: Should return empty when category is not found")
    void testUpdateCategoryNotFound() {

        given(categoryRepository.findById(categoryTest.getId())).willReturn(Optional.empty());

        Optional<CategoryResponseDto> result = categoryService.updateCategory(categoryTest.getId(), categoryRequestDtoTest);

        assertTrue(result.isEmpty(), "The result should be empty");

        verify(categoryRepository, times(1)).findById(categoryTest.getId());
        verify(categoryRepository, times(0)).save(categoryTest);
        verify(categoryMapper, times(0)).categoryToCategoryResponseDto(categoryTest);
    }

    @Test
    @DisplayName("Update Category: Should return empty when category is inactive")
    void testUpdateCategoryNotActive() {

        categoryTest.setActive(false);
        given(categoryRepository.findById(categoryTest.getId())).willReturn(Optional.of(categoryTest));

        Optional<CategoryResponseDto> result = categoryService.updateCategory(categoryTest.getId(), categoryRequestDtoTest);

        assertTrue(result.isEmpty(), "The result should be empty");

        verify(categoryRepository, times(1)).findById(categoryTest.getId());
        verify(categoryRepository, times(0)).save(categoryTest);
        verify(categoryMapper, times(0)).categoryToCategoryResponseDto(categoryTest);
    }

    @Test
    @DisplayName("Delete Category: Should delete existing category and return true")
    void testDeleteCategory() {

        given(categoryRepository.findById(categoryTest.getId())).willReturn(Optional.of(categoryTest));
        given(categoryRepository.save(categoryTest)).willReturn(categoryTest);

        Boolean result = categoryService.deleteCategory(categoryTest.getId());

        assertTrue(result);

        verify(categoryRepository, times(1)).findById(categoryTest.getId());
        verify(categoryRepository, times(1)).save(categoryTest);
    }

    @Test
    @DisplayName("Delete Category: Should return false when category not found")
    void testDeleteCategoryNotFound() {

        given(categoryRepository.findById(categoryTest.getId())).willReturn(Optional.empty());

        Boolean result = categoryService.deleteCategory(categoryTest.getId());

        assertFalse(result);

        verify(categoryRepository, times(1)).findById(categoryTest.getId());
        verify(categoryRepository, times(0)).delete(categoryTest);
    }

    @Test
    @DisplayName("Delete Category: Should return false when category is inactive")
    void testDeleteCategoryInactive() {

        categoryTest.setActive(false);
        given(categoryRepository.findById(categoryTest.getId())).willReturn(Optional.of(categoryTest));

        Boolean result = categoryService.deleteCategory(categoryTest.getId());

        assertFalse(result);

        verify(categoryRepository, times(1)).findById(categoryTest.getId());
        verify(categoryRepository, times(0)).delete(categoryTest);
    }

    @Test
    @DisplayName("List Categories by Account ID: Should return a page of active categories for a given accountId")
    void testListCategoryByAccountId() {

        given(categoryRepository.findAllByAccountIdAndActiveIsTrue(accountId, pageable)).willReturn(categoryPage);

        Page<Category> result = categoryService.listCategoryByAccountId(accountId, pageable);

        assertAll(() -> {
            assertFalse(result.isEmpty());
            assertEquals(2, result.getTotalElements());
        });

        Category category = result.getContent().getFirst();
        assertAll(() -> {
            assertEquals("Category 1", category.getTitle(), "The category title should be 'Category 1'");
            assertEquals("Description 1", category.getDescription(), "The category description should be 'Description 1'");
            assertEquals("image1.png", category.getImage(), "The category image should be 'image1.png'");
        });

        verify(categoryRepository, times(1)).findAllByAccountIdAndActiveIsTrue(accountId, pageable);
    }

    @Test
    @DisplayName("List Categories by Title: Should return a page of active categories for a given title")
    void testListCategoryByTitleActive() {

        given(categoryRepository.findAllByTitleIsLikeIgnoreCaseAndActive("%" + title + "%", true, pageable)).willReturn(categoryPage);

        Page<Category> result = categoryService.listCategoryByTitle(title, true, pageable);

        assertAll(() -> {
            assertFalse(result.isEmpty(), "The category page should not be empty");
            assertEquals(2, result.getTotalElements(), "The total number of elements should be 2");
        });

        Category category = result.getContent().getFirst();
        assertAll(() -> {
            assertEquals("Category 1", category.getTitle(), "The category title should be 'Category 1'");
            assertEquals("Description 1", category.getDescription(), "The category description should be 'Description 1'");
            assertEquals("image1.png", category.getImage(), "The category image should be 'image1.png'");
        });

        verify(categoryRepository, times(1)).findAllByTitleIsLikeIgnoreCaseAndActive("%" + title + "%", true, pageable);
    }

    @Test
    @DisplayName("List Categories by Title: Should return an empty page when no active categories match the title")
    void testListCategoryByTitleInactive() {

        given(categoryRepository.findAllByTitleIsLikeIgnoreCaseAndActive("%" + title + "%", false, pageable)).willReturn(Page.empty());

        Page<Category> result = categoryService.listCategoryByTitle(title, false, pageable);

        assertTrue(result.isEmpty(), "The category page should be empty");

        verify(categoryRepository, times(1)).findAllByTitleIsLikeIgnoreCaseAndActive("%" + title + "%", false, pageable);
    }
}