package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.output.CategoryResponseDto;
import org.skyline.mcq.application.mappings.CategoryMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.infrastructure.inputport.CategoryInputPort;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryInputPort {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PaginationHelper paginationHelper;

    @Override
    public CategoryResponseDto saveCategory(Category category) {
        return categoryMapper.categoryToCategoryResponseDto(categoryRepository.save(category));
    }

    @Override
    public Page<CategoryResponseDto> listCategories(UUID accountId, String title, Boolean isActive, Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = paginationHelper.buildPageRequest(pageNumber, pageSize);

        Page<Category> categoryPage;

        if (accountId != null && title == null) {
            categoryPage = listCategoryByAccountId(accountId, pageRequest);
        } else if (accountId == null && StringUtils.hasText(title)) {
            categoryPage = listCategoryByTitle(title, isActive, pageRequest);
        } else {
            categoryPage = categoryRepository.findAllByActive(isActive, pageRequest);
        }

        return categoryPage.map(categoryMapper::categoryToCategoryResponseDto);
    }

    @Override
    public Optional<CategoryResponseDto> updateCategory(UUID id, CategoryRequestDto category) {

        AtomicReference<Optional<CategoryResponseDto>> atomicReference = new AtomicReference<>();

        categoryRepository.findById(id).ifPresentOrElse(foundCategory -> {
            if (Boolean.TRUE.equals(foundCategory.getActive())) {
                foundCategory.setTitle(category.getTitle());
                foundCategory.setDescription(category.getDescription());
                foundCategory.setImage(category.getImage());

                Category updatedCategory = categoryRepository.save(foundCategory);
                CategoryResponseDto responseDto = categoryMapper.categoryToCategoryResponseDto(updatedCategory);

                atomicReference.set(Optional.of(responseDto));
            } else {
                atomicReference.set(Optional.empty());
            }
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    public Boolean deleteCategory(UUID id) {

        Optional<Category> category = categoryRepository.findById(id);

        if (category.isPresent()) {
            category.get().setActive(false);
            categoryRepository.save(category.get());
            return true;
        }

        return false;
    }

    public Page<Category> listCategoryByAccountId(UUID id, Pageable pageable) {
        return this.categoryRepository.findAllByAccountIdAndActiveIsTrue(id, pageable);
    }

    public Page<Category> listCategoryByTitle(String title, Boolean isActive, Pageable pageable) {
        return this.categoryRepository.findAllByTitleIsLikeIgnoreCaseAndActive("%" + title + "%", isActive, pageable);
    }
}
