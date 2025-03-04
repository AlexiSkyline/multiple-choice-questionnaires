package org.skyline.mcq.infrastructure.inputadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CategoryAPITest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private CategoryRequestDto categoryRequestDtoTest;

    private static final String CATEGORY_PATH = "/api/v1/categories";
    private static final String CATEGORY_PATH_ID = CATEGORY_PATH + "/{id}";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        Account accountTest = accountRepository.findAll().getFirst();
        categoryRequestDtoTest = CategoryRequestDto.builder()
                .title("Test Category")
                .description("Test Description")
                .image("Test Image")
                .accountId(accountTest.getId())
                .build();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Create Category: Should create a category and return 201 Created")
    void testCreateCategory() throws Exception {

        mockMvc.perform(post(CATEGORY_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDtoTest)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @DisplayName("Create Category: Should return 404 Not Found when account does not exist")
    void testCreateCategoryNotFound() throws Exception {

        categoryRequestDtoTest.setAccountId(UUID.randomUUID());
        mockMvc.perform(post(CATEGORY_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDtoTest)))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Get All Categories: Should return a list of categories and 200 OK")
    void testGetAllCategories() throws Exception {

        mockMvc.perform(get(CATEGORY_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()", is(4)));
    }

    @Test
    @DisplayName("Get Categories by Title: Should return categories matching the title and 200 OK")
    void testGetCategoryByTitle() throws Exception {

        mockMvc.perform(get(CATEGORY_PATH)
                        .param("title", "Language"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()", is(1)));
    }

    @Test
    @Rollback
    @DisplayName("Update Category: Should update a category and return 204 No Content")
    void testUpdateCategory() throws Exception {

        UUID categoryId = categoryRepository.findAll().getFirst().getId();
        mockMvc.perform(put(CATEGORY_PATH_ID, categoryId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDtoTest)))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Update Category: Should return 404 Not Found when category does not exist")
    void testUpdateCategoryNotFound() throws Exception {

        mockMvc.perform(put(CATEGORY_PATH_ID, UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDtoTest)))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Rollback
    @DisplayName("Delete Category: Should delete a category and return 204 No Content")
    void testDeleteCategory() throws Exception {

        UUID categoryId = categoryRepository.findAll().getFirst().getId();
        mockMvc.perform(delete(CATEGORY_PATH_ID, categoryId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Delete Category: Should return 404 Not Found when category does not exist")
    void testDeleteCategoryNotFound() throws Exception {

        mockMvc.perform(delete(CATEGORY_PATH_ID, UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
