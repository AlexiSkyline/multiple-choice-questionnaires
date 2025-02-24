package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.domain.Category;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface CategoryInputPort {

    Category saveCategory(Category category);
    Page<Category> listCategories(Integer pageNumber, Integer pageSize);
    Page<Category> listCategoriesByAccountId(UUID accountId, Integer pageNumber, Integer pageSize);
    Page<Category> findCategoriesByTitle(String title, Integer pageNumber, Integer pageSize);
    Optional<Category> updateCategory(UUID id, Category category);
    Boolean deleteCategory(UUID id);
}
