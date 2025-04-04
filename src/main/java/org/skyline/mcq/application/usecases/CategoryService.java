package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.output.CategoryResponseDto;
import org.skyline.mcq.application.mappings.CategoryMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.infrastructure.inputport.CategoryInputPort;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryInputPort {

    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final CategoryMapper categoryMapper;
    private final PaginationHelper paginationHelper;

    @Override
    @Transactional
    public Optional<CategoryResponseDto> saveCategory(UUID accountId, CategoryRequestDto category) {
        Optional<Account> account = accountRepository.findById(accountId).filter(Account::getActive);

        if (account.isEmpty()) return Optional.empty();

        Category newCategory = categoryMapper.categoryRequestDtoToCategory(category);
        newCategory.setAccount(account.get());

        return Optional.of(categoryMapper.categoryToCategoryResponseDto(categoryRepository.save(newCategory)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> listCategories(UUID accountId, String title, Boolean isActive, Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = paginationHelper.buildPageRequest(pageNumber, pageSize);

        Page<Category> categoryPage = null;

        if (accountId != null) {
            categoryPage = listCategoryByAccountId(accountId, pageRequest);
        } else if (StringUtils.hasText(title) && isActive != null) {
            categoryPage = listCategoryByTitle(title, isActive, pageRequest);
        } else if (isActive != null) {
            categoryPage = categoryRepository.findAllByActive(isActive, pageRequest);
        }

        if (categoryPage == null || categoryPage.isEmpty()) {
            return Page.empty();
        }

        return categoryPage.map(categoryMapper::categoryToCategoryResponseDto);
    }

    @Override
    @Transactional
    public Optional<CategoryResponseDto> updateCategory(UUID id, UUID accountId, CategoryRequestDto category) {

        AtomicReference<Optional<CategoryResponseDto>> atomicReference = new AtomicReference<>();

        categoryRepository.findByIdAndAccountId(id, accountId).ifPresentOrElse(foundCategory -> {
            if (Boolean.TRUE.equals(foundCategory.getActive())) {
                categoryMapper.updateCategoryFromCategoryRequestDto(category, foundCategory);
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
    @Transactional
    public Boolean deleteCategoryByIdAndByAccountId(UUID id, UUID accountId) {

        Optional<Category> category = categoryRepository.findByIdAndAccountId(id, accountId);

        if (category.isPresent() && Boolean.TRUE.equals(category.get().getActive())) {
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
