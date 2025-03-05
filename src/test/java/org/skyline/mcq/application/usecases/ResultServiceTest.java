package org.skyline.mcq.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.output.ResultResponseDto;
import org.skyline.mcq.application.mappings.ResultMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Result;
import org.skyline.mcq.infrastructure.outputport.ResultRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private ResultMapper resultMapper;

    @Mock
    private PaginationHelper paginationHelper;

    @InjectMocks
    private ResultService resultService;

    Result resultTest;
    ResultResponseDto resultResponseDtoTest;
    private Page<Result> resultPage;
    private PageRequest pageable;

    @BeforeEach
    void setUp() {

        resultTest = Result.builder()
                .id(UUID.randomUUID())
                .startTime(Timestamp.valueOf(LocalDateTime.now()))
                .endTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)))
                .totalPoints(100)
                .correctAnswers(10)
                .incorrectAnswers(0)
                .build();

        resultResponseDtoTest = ResultResponseDto.builder()
                .id(resultTest.getId())
                .startTime(resultTest.getStartTime())
                .endTime(resultTest.getEndTime())
                .totalPoints(resultTest.getTotalPoints())
                .correctAnswers(resultTest.getCorrectAnswers())
                .incorrectAnswers(resultTest.getIncorrectAnswers())
                .build();

        pageable = PageRequest.of(0, 10);
        resultPage = new PageImpl<>(Collections.singletonList(resultTest), pageable, 1);
    }

    @Test
    @DisplayName("Save Result: Should save a result and return the response DTO")
    void testSaveResult() {

        given(resultRepository.save(resultTest)).willReturn(resultTest);
        given(resultMapper.resultToResultResponseDto(resultTest)).willReturn(resultResponseDtoTest);

        ResultResponseDto resultResponseDto = resultService.saveResult(resultTest);

        assertAll(() -> {
            assertNotNull(resultResponseDto);
            assertEquals(resultResponseDtoTest, resultResponseDto);
        });

        verify(resultRepository).save(resultTest);
        verify(resultMapper).resultToResultResponseDto(resultTest);
    }

    @Test
    @DisplayName("Find Result by ID: Should return a result for a given ID")
    void testFindResultById() {

        given(resultRepository.findById(resultTest.getId())).willReturn(java.util.Optional.of(resultTest));
        given(resultMapper.resultToResultResponseDto(resultTest)).willReturn(resultResponseDtoTest);

        var resultResponseDto = resultService.findResultById(resultTest.getId());

        assertAll(() -> {
            assertNotNull(resultResponseDto);
            assertEquals(resultResponseDtoTest, resultResponseDto.get());
        });

        verify(resultRepository).findById(resultTest.getId());
        verify(resultMapper).resultToResultResponseDto(resultTest);
    }

    @Test
    @DisplayName("Find Result by ID: Should return an empty optional when result is not found")
    void testFindResultByIdNotFound() {

        given(resultRepository.findById(resultTest.getId())).willReturn(java.util.Optional.empty());

        var resultResponseDto = resultService.findResultById(resultTest.getId());

        assertAll(() -> {
            assertNotNull(resultResponseDto);
            assertTrue(resultResponseDto.isEmpty());
        });

        verify(resultRepository).findById(resultTest.getId());
        verify(resultMapper, never()).resultToResultResponseDto(resultTest);
    }

    @Test
    @DisplayName("List Results by Survey ID: Should return a page of results for a given survey ID")
    void testListResultBySurveyId() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(resultRepository.findAllByAccountId(resultTest.getId(), pageable)).willReturn(resultPage);
        given(resultMapper.resultToResultResponseDto(resultTest)).willReturn(resultResponseDtoTest);

        Page<ResultResponseDto> resultResponseDtoPage = resultService.listResultBySurveyId(resultTest.getId(), 0, 10);

        assertAll(() -> {
            assertNotNull(resultResponseDtoPage);
            assertFalse(resultResponseDtoPage.isEmpty());
            assertEquals(resultResponseDtoTest.getId(), resultResponseDtoPage.getContent().getFirst().getId());
            assertEquals(resultResponseDtoTest.getStartTime(), resultResponseDtoPage.getContent().getFirst().getStartTime());
            assertEquals(resultResponseDtoTest.getEndTime(), resultResponseDtoPage.getContent().getFirst().getEndTime());
            assertEquals(resultResponseDtoTest.getTotalPoints(), resultResponseDtoPage.getContent().getFirst().getTotalPoints());
            assertEquals(resultResponseDtoTest.getCorrectAnswers(), resultResponseDtoPage.getContent().getFirst().getCorrectAnswers());
            assertEquals(resultResponseDtoTest.getIncorrectAnswers(), resultResponseDtoPage.getContent().getFirst().getIncorrectAnswers());
        });

        verify(paginationHelper).buildPageRequest(0, 10);
        verify(resultRepository).findAllByAccountId(resultTest.getId(), pageable);
        verify(resultMapper).resultToResultResponseDto(resultTest);
    }

    @Test
    @DisplayName("List Results by Account ID: Should return a page of results for a given account ID")
    void testListResultByAccountId() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(resultRepository.findAllByAccountId(resultTest.getId(), pageable)).willReturn(resultPage);
        given(resultMapper.resultToResultResponseDto(resultTest)).willReturn(resultResponseDtoTest);

        Page<ResultResponseDto> resultResponseDtoPage = resultService.listResultByAccountId(resultTest.getId(), 0, 10);

        assertAll(() -> {
            assertNotNull(resultResponseDtoPage);
            assertFalse(resultResponseDtoPage.isEmpty());
            assertEquals(resultResponseDtoTest.getId(), resultResponseDtoPage.getContent().getFirst().getId());
            assertEquals(resultResponseDtoTest.getStartTime(), resultResponseDtoPage.getContent().getFirst().getStartTime());
            assertEquals(resultResponseDtoTest.getEndTime(), resultResponseDtoPage.getContent().getFirst().getEndTime());
            assertEquals(resultResponseDtoTest.getTotalPoints(), resultResponseDtoPage.getContent().getFirst().getTotalPoints());
            assertEquals(resultResponseDtoTest.getCorrectAnswers(), resultResponseDtoPage.getContent().getFirst().getCorrectAnswers());
            assertEquals(resultResponseDtoTest.getIncorrectAnswers(), resultResponseDtoPage.getContent().getFirst().getIncorrectAnswers());
        });

        verify(paginationHelper).buildPageRequest(0, 10);
        verify(resultRepository).findAllByAccountId(resultTest.getId(), pageable);
        verify(resultMapper).resultToResultResponseDto(resultTest);
    }

    @Test
    @DisplayName("List Results by Survey ID and Account ID: Should return a page of results for a given survey ID and account ID")
    void testListResultBySurveyIdAndAccountId() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(resultRepository.findAllByAccountIdAndSurveyId(resultTest.getId(), resultTest.getId(), pageable)).willReturn(resultPage);
        given(resultMapper.resultToResultResponseDto(resultTest)).willReturn(resultResponseDtoTest);

        Page<ResultResponseDto> resultResponseDtoPage = resultService.listResultBySurveyIdAndAccountId(resultTest.getId(), resultTest.getId(), 0, 10);

        assertAll(() -> {
            assertNotNull(resultResponseDtoPage);
            assertFalse(resultResponseDtoPage.isEmpty());
            assertEquals(resultResponseDtoTest.getId(), resultResponseDtoPage.getContent().getFirst().getId());
            assertEquals(resultResponseDtoTest.getStartTime(), resultResponseDtoPage.getContent().getFirst().getStartTime());
            assertEquals(resultResponseDtoTest.getEndTime(), resultResponseDtoPage.getContent().getFirst().getEndTime());
            assertEquals(resultResponseDtoTest.getTotalPoints(), resultResponseDtoPage.getContent().getFirst().getTotalPoints());
            assertEquals(resultResponseDtoTest.getCorrectAnswers(), resultResponseDtoPage.getContent().getFirst().getCorrectAnswers());
            assertEquals(resultResponseDtoTest.getIncorrectAnswers(), resultResponseDtoPage.getContent().getFirst().getIncorrectAnswers());
        });

        verify(paginationHelper).buildPageRequest(0, 10);
        verify(resultRepository).findAllByAccountIdAndSurveyId(resultTest.getId(), resultTest.getId(), pageable);
        verify(resultMapper).resultToResultResponseDto(resultTest);
    }
}