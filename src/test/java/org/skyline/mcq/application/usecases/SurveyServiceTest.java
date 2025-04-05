package org.skyline.mcq.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.input.SurveyRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.application.mappings.AccountMapper;
import org.skyline.mcq.application.mappings.SurveyMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.skyline.mcq.infrastructure.outputport.SurveyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PaginationHelper paginationHelper;

    @Mock
    private SurveyMapper surveyMapper;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private SurveyService surveyService;

    private Survey surveyTest;
    private SurveyResponseDto surveyResponseDtoTest;
    private SurveyRequestDto surveyRequestDtoTest;
    private Account accountTest;
    private Category categoryTest;
    private SurveyUpdateRequestDto surveyUpdateRequestDtoTest;
    private PageRequest pageable;
    private List<AccountSummaryDto> accounts;
    private Page<Account> accountPage;
    private Page<Survey> surveyPage;

    @BeforeEach
    void setUp() {

        surveyTest = Survey.builder()
                .id(UUID.randomUUID())
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .active(true)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .build();

        surveyResponseDtoTest = SurveyResponseDto.builder()
                .id(UUID.randomUUID())
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .build();
        pageable = PageRequest.of(0, 10);

        accountTest = Account.builder()
                .firstName("Sky")
                .lastName("Taylor")
                .username("sky_responder")
                .email("sky.taylor@example.com")
                .password("SkyPassword123")
                .profileImage("account1.jpg")
                .description("New Sky responder")
                .build();

        categoryTest = Category.builder()
                .id(UUID.randomUUID())
                .title("New Category")
                .description("New Category")
                .image("New_Category.png")
                .active(true)
                .build();

        surveyRequestDtoTest = SurveyRequestDto.builder()
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .categoryId(categoryTest.getId())
                .build();

        surveyUpdateRequestDtoTest = SurveyUpdateRequestDto.builder()
                .title("new title")
                .description("new description")
                .image("survey.png")
                .maxPoints(10)
                .questionCount(5)
                .timeLimit(3600)
                .attempts(1)
                .hasRestrictedAccess(true)
                .build();

        AccountSummaryDto accountSummaryDtoTest = AccountSummaryDto.builder()
                .id(UUID.randomUUID())
                .firstName("Sky")
                .lastName("Taylor")
                .username("sky_responder")
                .email("sky@gmail.com")
                .build();

        accounts = Collections.singletonList(accountSummaryDtoTest);
        accountPage = new PageImpl<>(Collections.singletonList(accountTest), pageable, 1);
        surveyPage = new PageImpl<>(Collections.singletonList(surveyTest), pageable, 1);
    }

    @Test
    @DisplayName("Save Survey: Should save a survey and return the response DTO")
    void testSaveSurvey() {

        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.of(accountTest));
        given(categoryRepository.findById(categoryTest.getId())).willReturn(Optional.of(categoryTest));
        given(surveyRepository.save(surveyTest)).willReturn(surveyTest);
        given(surveyMapper.surveyToSurveyResponseDto(surveyTest)).willReturn(surveyResponseDtoTest);
        given(surveyMapper.surveyRequesttDtoToSurvey(surveyRequestDtoTest)).willReturn(surveyTest);

        var result = surveyService.saveSurvey(accountTest.getId(), surveyRequestDtoTest);

        assertAll("Save Survey",
                () -> assertNotNull(result, "The result should not be null"),
                () -> assertTrue(result.isPresent(), "The result should be present"),
                () -> assertEquals(surveyResponseDtoTest, result.get(), "The result should match the expected DTO")
        );

        verify(accountRepository).findById(accountTest.getId());
        verify(categoryRepository).findById(categoryTest.getId());
        verify(surveyRepository).save(surveyTest);
        verify(surveyMapper).surveyToSurveyResponseDto(surveyTest);
    }

    @Test
    @DisplayName("Save Survey: Should not save a survey when the account does not exist")
    void testSaveSurveyAccountNotFound() {

        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.empty());
        given(categoryRepository.findById(categoryTest.getId())).willReturn(Optional.of(categoryTest));

        var result = surveyService.saveSurvey(accountTest.getId(), surveyRequestDtoTest);

        assertAll("Save Survey - Account Not Found",
                () -> assertNotNull(result, "The result should not be null"),
                () -> assertTrue(result.isEmpty(), "The result should be empty")
        );

        verify(accountRepository).findById(accountTest.getId());
        verify(categoryRepository).findById(categoryTest.getId());
        verify(surveyRepository, never()).save(surveyTest);
        verify(surveyMapper, never()).surveyToSurveyResponseDto(surveyTest);
    }

    @Test
    @DisplayName("Save Survey: Should not save a survey when the category does not exist")
    void testSaveSurveyCategoryNotFound() {

        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.of(accountTest));
        given(categoryRepository.findById(categoryTest.getId())).willReturn(Optional.empty());

        var result = surveyService.saveSurvey(accountTest.getId(), surveyRequestDtoTest);

        assertAll("Save Survey - Category Not Found",
                () -> assertNotNull(result, "The result should not be null"),
                () -> assertTrue(result.isEmpty(), "The result should be empty")
        );

        verify(accountRepository).findById(accountTest.getId());
        verify(categoryRepository).findById(categoryTest.getId());
        verify(surveyRepository, never()).save(surveyTest);
        verify(surveyMapper, never()).surveyToSurveyResponseDto(surveyTest);
    }

    @Test
    @DisplayName("Find Survey by ID: Should return the survey when it exists and is active")
    void testFindSurveyById() {

        given(surveyRepository.findById(surveyTest.getId())).willReturn(Optional.of(surveyTest));
        given(surveyMapper.surveyToSurveyResponseDto(surveyTest)).willReturn(surveyResponseDtoTest);

        var result = surveyService.findSurveyById(surveyTest.getId());

        assertAll("Find Survey by ID",
                () -> assertTrue(result.isPresent(), "The survey should be found"),
                () -> assertEquals(surveyResponseDtoTest, result.get(), "The returned DTO should match the expected one")
        );

        verify(surveyRepository).findById(surveyTest.getId());
        verify(surveyMapper).surveyToSurveyResponseDto(surveyTest);
    }

    @Test
    @DisplayName("Find Survey by ID: Should return empty when the survey does not exist")
    void testFindSurveyByIdNotFound() {

        given(surveyRepository.findById(surveyTest.getId())).willReturn(Optional.empty());

        var result = surveyService.findSurveyById(surveyTest.getId());

        assertAll("Find Survey by ID - Not Found",
                () -> assertTrue(result.isEmpty(), "The result should be empty since the survey does not exist")
        );

        verify(surveyRepository).findById(surveyTest.getId());
        verify(surveyMapper, never()).surveyToSurveyResponseDto(surveyTest);
    }

    @Test
    @DisplayName("Find Survey by ID: Should return empty when the survey is inactive")
    void testFindSurveyByIdInactive() {

        surveyTest.setActive(false);
        given(surveyRepository.findById(surveyTest.getId())).willReturn(Optional.of(surveyTest));

        var result = surveyService.findSurveyById(surveyTest.getId());

        assertAll("Find Survey by ID - Inactive Survey",
                () -> assertTrue(result.isEmpty(), "The result should be empty since the survey is inactive")
        );

        verify(surveyRepository).findById(surveyTest.getId());
        verify(surveyMapper, never()).surveyToSurveyResponseDto(surveyTest);
    }

    @Test
    @DisplayName("List Surveys: Should return a page of surveys based on the provided filters")
    void testListSurvey() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(surveyMapper.surveyToSurveyResponseDto(any(Survey.class))).willReturn(surveyResponseDtoTest);
        given(surveyRepository.findAll(any(Specification.class), eq(pageable))).willReturn(surveyPage);

        var result = surveyService.listSurveys(UUID.randomUUID(), true, false, UUID.randomUUID(), true, 0, 10);

        assertAll("List Surveys",
                () -> assertNotNull(result, "The result should not be null"),
                () -> assertFalse(result.getContent().isEmpty(), "The result should contain surveys"),
                () -> assertEquals(1, result.getContent().size(), "The result should contain exactly one survey")
        );

        verify(surveyRepository).findAll(any(Specification.class), eq(pageable));
        verify(surveyMapper).surveyToSurveyResponseDto(any(Survey.class));
    }

    @Test
    @DisplayName("List Accounts by Survey ID: Should return active accounts when both survey and account are active")
    void testListAccountsBySurveyIdSurveyActiveAndAccountActive() {
        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(surveyRepository.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), true, true, pageable)).willReturn(accountPage);
        given(accountMapper.accountToAccountResponseDto(accountTest)).willReturn(accounts.getFirst());

        var result = surveyService.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), true, true, 0, 10);

        assertNotNull(result, "Result should not be null");
        assertEquals(accounts.size(), result.getContent().size(), "The number of accounts should match");

        verify(surveyRepository).listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), true, true, pageable);
        verify(accountMapper).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("List Accounts by Survey ID: Should return accounts when survey is active and account is inactive")
    void testListAccountsBySurveyIdSurveyActiveAndAccountInactive() {
        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(surveyRepository.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), true, false, pageable)).willReturn(accountPage);
        given(accountMapper.accountToAccountResponseDto(accountTest)).willReturn(accounts.getFirst());

        var result = surveyService.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), true, false, 0, 10);

        assertNotNull(result, "Result should not be null");
        assertEquals(accounts.size(), result.getContent().size(), "The number of accounts should match");

        verify(surveyRepository).listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), true, false, pageable);
        verify(accountMapper).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("List Accounts by Survey ID: Should return accounts when survey is inactive and account is active")
    void testListAccountsBySurveyIdSurveyInactiveAndAccountActive() {
        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(surveyRepository.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), false, true, pageable)).willReturn(accountPage);
        given(accountMapper.accountToAccountResponseDto(accountTest)).willReturn(accounts.getFirst());

        var result = surveyService.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), false, true, 0, 10);

        assertNotNull(result, "Result should not be null");
        assertEquals(accounts.size(), result.getContent().size(), "The number of accounts should match");

        verify(surveyRepository).listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), false, true, pageable);
        verify(accountMapper).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("List Accounts by Survey ID: Should return accounts when both survey and account are inactive")
    void testListAccountsBySurveyIdSurveyInactiveAndAccountInactive() {
        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(surveyRepository.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), false, false, pageable)).willReturn(accountPage);
        given(accountMapper.accountToAccountResponseDto(accountTest)).willReturn(accounts.getFirst());

        var result = surveyService.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), false, false, 0, 10);

        assertNotNull(result, "Result should not be null");
        assertEquals(accounts.size(), result.getContent().size(), "The number of accounts should match");

        verify(surveyRepository).listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyTest.getId(), false, false, pageable);
        verify(accountMapper).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Update Survey: Should update survey when it exists and is active")
    void testUpdateSurvey() {
        given(surveyRepository.findByIdAndAccountId(surveyTest.getId(), accountTest.getId())).willReturn(Optional.of(surveyTest));
        given(surveyRepository.save(surveyTest)).willReturn(surveyTest);
        given(surveyMapper.surveyToSurveyResponseDto(surveyTest)).willReturn(surveyResponseDtoTest);

        var result = surveyService.updateSurvey(surveyTest.getId(), accountTest.getId(), surveyUpdateRequestDtoTest);

        assertTrue(result.isPresent(), "Survey should be updated successfully");

        verify(surveyRepository).findByIdAndAccountId(surveyTest.getId(), accountTest.getId());
        verify(surveyRepository).save(surveyTest);
        verify(surveyMapper).surveyToSurveyResponseDto(surveyTest);
    }

    @Test
    @DisplayName("Update Survey: Should not update survey when it is not found")
    void testUpdateSurveyNotFound() {
        given(surveyRepository.findByIdAndAccountId(surveyTest.getId(), accountTest.getId())).willReturn(Optional.empty());

        var result = surveyService.updateSurvey(surveyTest.getId(), accountTest.getId(), surveyUpdateRequestDtoTest);

        assertTrue(result.isEmpty(), "Survey should not be updated if not found");

        verify(surveyRepository).findByIdAndAccountId(surveyTest.getId(), accountTest.getId());
        verify(surveyRepository, never()).save(surveyTest);
        verify(surveyMapper, never()).surveyToSurveyResponseDto(surveyTest);
    }

    @Test
    @DisplayName("Update Survey: Should not update survey when it is inactive")
    void testUpdateSurveyInactive() {
        surveyTest.setActive(false);
        given(surveyRepository.findByIdAndAccountId(surveyTest.getId(), accountTest.getId())).willReturn(Optional.of(surveyTest));

        var result = surveyService.updateSurvey(surveyTest.getId(), accountTest.getId(), surveyUpdateRequestDtoTest);

        assertTrue(result.isEmpty(), "Survey should not be updated if it is inactive");

        verify(surveyRepository).findByIdAndAccountId(surveyTest.getId(), accountTest.getId());
        verify(surveyRepository, never()).save(surveyTest);
        verify(surveyMapper, never()).surveyToSurveyResponseDto(surveyTest);
    }

    @Test
    @DisplayName("Delete Survey: Should delete survey when it exists")
    void shouldDeleteSurveyWhenExists() {
        given(surveyRepository.findByIdAndAccountId(surveyTest.getId(), accountTest.getId())).willReturn(Optional.of(surveyTest));
        given(surveyRepository.save(surveyTest)).willReturn(surveyTest);

        boolean result = surveyService.deleteSurvey(surveyTest.getId(), accountTest.getId());

        assertTrue(result, "Survey should be deleted successfully");

        verify(surveyRepository).findByIdAndAccountId(surveyTest.getId(), accountTest.getId());
        verify(surveyRepository).save(surveyTest);
    }

    @Test
    @DisplayName("Delete Survey: Should not delete survey when it is not found")
    void shouldNotDeleteSurveyWhenNotFound() {
        given(surveyRepository.findByIdAndAccountId(surveyTest.getId(), accountTest.getId())).willReturn(Optional.empty());

        boolean result = surveyService.deleteSurvey(surveyTest.getId(), accountTest.getId());

        assertFalse(result, "Survey should not be deleted if not found");

        verify(surveyRepository).findByIdAndAccountId(surveyTest.getId(), accountTest.getId());
        verify(surveyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete Survey: Should not delete survey when it is already inactive")
    void shouldNotDeleteSurveyWhenAlreadyInactive() {
        surveyTest.setActive(false);
        given(surveyRepository.findByIdAndAccountId(surveyTest.getId(), accountTest.getId())).willReturn(Optional.of(surveyTest));

        boolean result = surveyService.deleteSurvey(surveyTest.getId(), accountTest.getId());

        assertFalse(result, "Survey should not be deleted if already inactive");

        verify(surveyRepository).findByIdAndAccountId(surveyTest.getId(), accountTest.getId());
        verify(surveyRepository, never()).save(any());
    }
}