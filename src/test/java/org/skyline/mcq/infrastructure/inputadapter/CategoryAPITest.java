package org.skyline.mcq.infrastructure.inputadapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.input.SignUpRequestDto;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.infrastructure.inputport.AccountInputPort;
import org.skyline.mcq.infrastructure.inputport.CategoryInputPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryAPITest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final String CATEGORY_PATH = "/api/v1/categories";
    private static final String CATEGORY_PATH_ID = CATEGORY_PATH + "/{id}";
    private static final String CREATOR_PATH = CATEGORY_PATH + "/creator";
    private static final String ADMIN_PATH = CATEGORY_PATH + "/admin";

    private static final String TEST_TITLE = "Test Category";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String TEST_IMAGE = "Test Image";
    private static final String SEARCH_TITLE = "Language";

    private static String tokenCreator;
    private static String tokenAdmin;
    private static UUID categoryIdA;
    private static UUID categoryIdB;

    private CategoryRequestDto categoryRequestDtoTest;

    @BeforeAll
    static void beforeAll(
            @Autowired AuthAPI authAPI,
            @Autowired CategoryInputPort categoryInputPort,
            @Autowired AccountInputPort accountInputPort) {

        SignUpRequestDto testCreator = buildSignUpRequest(
                "CreatorJohn99Test", "lastname creator", "creator.john99.smith.test@gmail.com");

        SignUpRequestDto testAdmin = buildSignUpRequest(
                "AdminJohn99Test", "lastname admin", "admin.john99.smith.test@gmail.com");

        tokenCreator = registerUserAndGetToken(authAPI, testCreator, TypeRole.ROLE_SURVEY_CREATOR);
        tokenAdmin = registerUserAndGetToken(authAPI, testAdmin, TypeRole.ROLE_ADMIN);

        accountInputPort.getUserByEmail(testCreator.getEmail())
                .ifPresent(account -> {
                    CategoryRequestDto categoryRequestDto = buildTestCategoryRequestDto();

                    categoryInputPort.saveCategory(account.getId(), categoryRequestDto)
                            .ifPresent(category -> categoryIdA = category.getId());

                    categoryInputPort.saveCategory(account.getId(), categoryRequestDto)
                            .ifPresent(category -> categoryIdB = category.getId());
                });
    }

    @BeforeEach
    void setUp() {
        categoryRequestDtoTest = buildTestCategoryRequestDto();
    }

    private static SignUpRequestDto buildSignUpRequest(String firstName, String lastName, String email) {
        return SignUpRequestDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(firstName)
                .email(email)
                .password("password")
                .build();
    }

    private static String registerUserAndGetToken(AuthAPI authAPI, SignUpRequestDto request, TypeRole role) {
        return Objects.requireNonNull(
                        authAPI.registerUserWithRole(request, role).getBody())
                .getAccessToken();
    }

    private static CategoryRequestDto buildTestCategoryRequestDto() {
        return CategoryRequestDto.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .image(TEST_IMAGE)
                .build();
    }

    @Test
    @DisplayName("Create Category: Should create a category and return 201 Created")
    void testCreateCategory() throws Exception {
        mockMvc.perform(post(CATEGORY_PATH)
                        .header("Authorization", "Bearer " + tokenCreator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDtoTest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").value(TEST_TITLE))
                .andExpect(jsonPath("$.data.description").value(TEST_DESCRIPTION));
    }

    @Test
    @DisplayName("Get All Categories: Should return a list of categories and 200 OK")
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get(CATEGORY_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").isNotEmpty());
    }

    @Test
    @DisplayName("Get Categories by Title: Should return categories matching the title and 200 OK")
    void testGetCategoryByTitle() throws Exception {
        mockMvc.perform(get(CATEGORY_PATH)
                        .param("title", SEARCH_TITLE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").isNotEmpty());
    }

    @Test
    @DisplayName("Get All Categories (Creator): Should return creator's categories and 200 OK")
    void testGetAllCategoriesCreator() throws Exception {
        mockMvc.perform(get(CREATOR_PATH)
                        .header("Authorization", "Bearer " + tokenCreator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").isNotEmpty());
    }

    @Test
    @DisplayName("Get All Categories (Admin): Should return all categories and 200 OK")
    void testGetAllCategoriesAdmin() throws Exception {
        mockMvc.perform(get(ADMIN_PATH)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").isNotEmpty());
    }

    @Test
    @DisplayName("Update Category: Should update a category and return 204 No Content")
    void testUpdateCategory() throws Exception {
        mockMvc.perform(put(CATEGORY_PATH_ID, categoryIdA)
                        .header("Authorization", "Bearer " + tokenCreator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDtoTest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Update Category: Should return 404 Not Found when category does not exist")
    void testUpdateCategoryNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        String expectedMessage = String.format("Category with identifier '%s' was not found. Please provide a valid category ID", randomId);

        mockMvc.perform(put(CATEGORY_PATH_ID, randomId)
                        .header("Authorization", "Bearer " + tokenCreator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDtoTest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Delete Category: Should delete a category and return 204 No Content")
    void testDeleteCategory() throws Exception {
        mockMvc.perform(delete(CATEGORY_PATH_ID, categoryIdB)
                        .header("Authorization", "Bearer " + tokenCreator)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete Category: Should return 404 Not Found when category does not exist")
    void testDeleteCategoryNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        String expectedMessage = String.format("Category with identifier '%s' was not found. Please provide a valid category ID", randomId);

        mockMvc.perform(delete(CATEGORY_PATH_ID, randomId)
                        .header("Authorization", "Bearer " + tokenCreator))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.status").value(404));
    }
}