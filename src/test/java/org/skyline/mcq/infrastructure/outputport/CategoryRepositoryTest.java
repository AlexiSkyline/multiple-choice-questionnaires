package org.skyline.mcq.infrastructure.outputport;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.infrastructure.bootstrap.BootstrapData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ BootstrapData.class })
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    AccountRepository accountRepository;
    Account accountTest;

    @BeforeEach
    void setUp() {
        accountTest = accountRepository.findAll().getFirst();
    }

    @Test
    @Transactional
    void testSaveCategory() {

        var category = Category.builder()
                .title("New Category")
                .description("New Category")
                .active(true)
                .image("New_Category.png")
                .build();

        var savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory.getId());
        assertEquals("New Category", savedCategory.getTitle());
    }

    @Test
    void testFindAllByTitleActive() {

        var categories = categoryRepository.findAllByTitleIsLikeIgnoreCaseAndActive("%academic%", true, null);

        assertNotNull(categories);
        assertFalse(categories.getContent().isEmpty());
    }

    @Test
    void testFindAllByTitleDisActive() {

        var categories = categoryRepository.findAllByTitleIsLikeIgnoreCaseAndActive("%academic%", false, null);

        assertNotNull(categories);
        assertTrue(categories.getContent().isEmpty());
    }

    @Test
    void testFindAllByTitleNoResults() {

        var categories = categoryRepository.findAllByTitleIsLikeIgnoreCaseAndActive("%nonexistent%", true, null);

        assertNotNull(categories);
        assertTrue(categories.getContent().isEmpty(), "Expected no categories to be returned");
    }

    @Test
    void testFindAllByUserId() {

        var categories = categoryRepository.findAllByAccountIdAndActiveIsTrue(accountTest.getId(), null);

        assertFalse(categories.getContent().isEmpty(), "Expected categories to be returned");
        assertTrue(categories.getContent().stream().allMatch(c -> c.getAccount().equals(accountTest)),
                "Expected all categories to be associated with the correct account");
    }

    @Test
    void testFindAllByUserIdNoResults() {

        var emptyAccount = new Account();
        emptyAccount.setId(UUID.randomUUID());

        var categories = categoryRepository.findAllByAccountIdAndActiveIsTrue(emptyAccount.getId(), null);

        assertTrue(categories.getContent().isEmpty(), "Expected no categories for this account");
    }

    @Test
    @Transactional
    void testUpdateCategory() {

        var category = Category.builder()
                .title("New Category")
                .description("Description")
                .image("New_Category.png")
                .build();

        var savedCategory = categoryRepository.save(category);

        savedCategory.setTitle("Updated Category");
        var updatedCategory = categoryRepository.save(savedCategory);

        assertEquals("Updated Category", updatedCategory.getTitle());
    }

    @Test
    @Transactional
    void testDeleteCategory() {

        var category = Category.builder()
                .title("Category to delete")
                .description("Delete this category")
                .image("delete_category.png")
                .build();

        var savedCategory = categoryRepository.save(category);
        savedCategory.setActive(false);

        categoryRepository.save(savedCategory);

        var categoryDeleted = categoryRepository.findById(savedCategory.getId()).orElseThrow(() -> new RuntimeException("Category not found"));

        assertNotNull(categoryDeleted);
        assertFalse(categoryDeleted.getActive());
    }
}