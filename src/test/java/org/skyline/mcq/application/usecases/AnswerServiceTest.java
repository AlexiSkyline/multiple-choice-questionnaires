package org.skyline.mcq.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.output.AnswerResponseDto;
import org.skyline.mcq.application.mappings.AnswerMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Answer;
import org.skyline.mcq.domain.models.Question;
import org.skyline.mcq.infrastructure.outputport.AnswerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private PaginationHelper paginationHelper;

    @Mock
    private AnswerMapper answerMapper;

    @InjectMocks
    private AnswerService answerService;

    private Answer answerTest;
    private AnswerResponseDto answerResponseDto;
    private PageRequest pageable;
    private Page<Answer> answersPage;

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

        Question questionTest = Question.builder()
                .content("Example new Question")
                .image("question.jpg")
                .points(5)
                .allowedAnswers(2)
                .options("{a, b, c, d, e}")
                .correctAnswers("{b, d}")
                .build();

        answerTest = Answer.builder()
                .id(UUID.randomUUID())
                .account(accountTest)
                .question(questionTest)
                .userAnswers("{b, d}")
                .isCorrect(true)
                .points(20)
                .build();

        answerResponseDto = AnswerResponseDto.builder()
                .id(UUID.randomUUID())
                .userAnswers("{a, b}")
                .isCorrect(true)
                .points(20)
                .build();

        pageable = PageRequest.of(0, 10);
        answersPage = new PageImpl<>(Collections.singletonList(answerTest), pageable, 1);
    }

    @Test
    @DisplayName("Save Answer: Should save an answer and return the response DTO")
    void testSaveAnswer() {

        given(answerRepository.save(answerTest)).willReturn(answerTest);
        given(answerMapper.answerToAnswerResponseDto(answerTest)).willReturn(answerResponseDto);

        var result = answerService.saveAnswer(answerTest);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(answerResponseDto.getId(), result.getId());
            assertEquals(answerResponseDto.getUserAnswers(), result.getUserAnswers());
            assertEquals(answerResponseDto.getIsCorrect(), result.getIsCorrect());
            assertEquals(answerResponseDto.getPoints(), result.getPoints());
        });

        verify(answerRepository).save(answerTest);
        verify(answerMapper).answerToAnswerResponseDto(answerTest);
    }

    @Test
    @DisplayName("List Answers by Result ID: Should return a page of answers for a given result ID")
    void testListAnswerByResultId() {

        given(paginationHelper.buildPageRequest(0, 10)).willReturn(pageable);
        given(answerRepository.findAllByResultId(answerTest.getId(), pageable)).willReturn(answersPage);
        given(answerMapper.answerToAnswerResponseDto(answerTest)).willReturn(answerResponseDto);

        var result = answerService.listAnswerByResultId(answerTest.getId(), 0, 10);

        assertAll(() -> {
            assertNotNull(result);
            assertFalse(result.getContent().isEmpty());
            assertEquals(answerResponseDto.getId(), result.getContent().getFirst().getId());
            assertEquals(answerResponseDto.getUserAnswers(), result.getContent().getFirst().getUserAnswers());
            assertEquals(answerResponseDto.getIsCorrect(), result.getContent().getFirst().getIsCorrect());
            assertEquals(answerResponseDto.getPoints(), result.getContent().getFirst().getPoints());
        });

        verify(answerRepository).findAllByResultId(answerTest.getId(), pageable);
        verify(answerMapper).answerToAnswerResponseDto(answerTest);
    }
}