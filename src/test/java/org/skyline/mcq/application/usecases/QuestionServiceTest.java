package org.skyline.mcq.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.input.QuestionRequestDto;
import org.skyline.mcq.application.dtos.input.QuestionUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.QuestionResponseDto;
import org.skyline.mcq.application.mappings.QuestionMapper;
import org.skyline.mcq.domain.models.Question;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.infrastructure.outputport.QuestionRepository;
import org.skyline.mcq.infrastructure.outputport.SurveyRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private QuestionService questionService;

    private Survey surveyTest;
    private Question questionTest;
    private QuestionResponseDto questionResponseDtoTest;
    private QuestionRequestDto questionRequestDtoTest;
    private QuestionUpdateRequestDto questionUpdateRequestDto;

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

        questionTest = Question.builder()
                .id(UUID.randomUUID())
                .content("Example new Question")
                .image("question.jpg")
                .points(5)
                .allowedAnswers(2)
                .options("{a, b, c, d, e}")
                .correctAnswers("{b, d}")
                .build();

        questionResponseDtoTest = QuestionResponseDto.builder()
                .id(questionTest.getId())
                .content("Example new Question")
                .image("question.jpg")
                .points(5)
                .allowedAnswers(2)
                .options("{a, b, c, d, e}")
                .correctAnswers("{b, d}")
                .build();

        questionRequestDtoTest = QuestionRequestDto.builder()
                .content("Example new Question")
                .image("question.jpg")
                .points(5)
                .allowedAnswers(2)
                .options("{a, b, c, d, e}")
                .correctAnswers("{b, d}")
                .surveyId(surveyTest.getId())
                .build();

        questionUpdateRequestDto = QuestionUpdateRequestDto.builder()
                .content("Example new Question")
                .image("question.jpg")
                .points(5)
                .allowedAnswers(2)
                .options("{a, b, c, d, e}")
                .correctAnswers("{b, d}")
                .build();
    }

    @Test
    @DisplayName("Save Question: Should save a question and return the response DTO")
    void testSaveQuestion() {

        given(surveyRepository.findById(questionRequestDtoTest.getSurveyId())).willReturn(Optional.of(surveyTest));
        given(questionMapper.questionRequestDtoToQuestion(questionRequestDtoTest)).willReturn(questionTest);
        given(questionRepository.save(questionTest)).willReturn(questionTest);
        given(questionMapper.questionToQuestionResponseDto(questionTest)).willReturn(questionResponseDtoTest);

        var result = questionService.saveQuestion(questionRequestDtoTest);

        assertAll(() -> {
            assertNotNull(result);
            assertTrue(result.isPresent());
            assertEquals(questionResponseDtoTest, result.get());
        });

        verify(surveyRepository).findById(questionRequestDtoTest.getSurveyId());
        verify(questionMapper).questionRequestDtoToQuestion(questionRequestDtoTest);
        verify(questionRepository).save(questionTest);
        verify(questionMapper).questionToQuestionResponseDto(questionTest);
    }

    @Test
    @DisplayName("Save Question: Should return empty when the survey is not found")
    void testSaveQuestionNotFoundSurvey() {

        given(surveyRepository.findById(questionRequestDtoTest.getSurveyId())).willReturn(Optional.empty());

        var result = questionService.saveQuestion(questionRequestDtoTest);

        assertAll(() -> {
            assertNotNull(result);
            assertFalse(result.isPresent());
        });

        verify(surveyRepository).findById(questionRequestDtoTest.getSurveyId());
        verify(questionRepository, never()).save(questionTest);
        verify(questionMapper, never()).questionToQuestionResponseDto(questionTest);
    }

    @Test
    @DisplayName("Find Question by ID: Should return the question when it exists")
    void testFindQuestionById() {

        given(questionRepository.findById(questionTest.getId())).willReturn(Optional.of(questionTest));
        given(questionMapper.questionToQuestionResponseDto(questionTest)).willReturn(questionResponseDtoTest);

        var result = questionService.findQuestionById(questionTest.getId());

        assertAll(() -> {
            assertNotNull(result);
            assertTrue(result.isPresent());
            assertEquals(questionResponseDtoTest, result.get());
        });

        verify(questionRepository).findById(questionTest.getId());
        verify(questionMapper).questionToQuestionResponseDto(questionTest);
    }

    @Test
    @DisplayName("Find Question by ID: Should return empty when the question does not exist")
    void testFindQuestionByIdNotFound() {

        given(questionRepository.findById(questionTest.getId())).willReturn(Optional.empty());

        var result = questionService.findQuestionById(questionTest.getId());

        assertAll(() -> {
            assertNotNull(result);
            assertFalse(result.isPresent());
        });

        verify(questionRepository).findById(questionTest.getId());
        verify(questionMapper, never()).questionToQuestionResponseDto(questionTest);
    }

    @Test
    @DisplayName("Update Question: Should update and return the question when it exists")
    void testUpdateQuestion() {

        given(questionRepository.findById(questionTest.getId())).willReturn(Optional.of(questionTest));
        given(questionRepository.save(questionTest)).willReturn(questionTest);
        given(questionMapper.questionToQuestionResponseDto(questionTest)).willReturn(questionResponseDtoTest);

        var result = questionService.updateQuestion(questionTest.getId(), questionUpdateRequestDto);

        assertAll(() -> {
            assertNotNull(result);
            assertTrue(result.isPresent());
            assertEquals(questionResponseDtoTest, result.get());
        });

        verify(questionRepository).findById(questionTest.getId());
        verify(questionRepository).save(questionTest);
        verify(questionMapper).updateQuestionFromQuestionRequestDto(questionUpdateRequestDto, questionTest);
        verify(questionMapper).questionToQuestionResponseDto(questionTest);
    }

    @Test
    @DisplayName("Update Question: Should return empty when the question does not exist")
    void updateQuestionNotFound() {

        given(questionRepository.findById(questionTest.getId())).willReturn(Optional.empty());

        var result = questionService.updateQuestion(questionTest.getId(), questionUpdateRequestDto);

        assertAll(() -> {
            assertNotNull(result);
            assertFalse(result.isPresent());
        });

        verify(questionRepository).findById(questionTest.getId());
        verify(questionRepository, never()).save(questionTest);
        verify(questionMapper, never()).updateQuestionFromQuestionRequestDto(questionUpdateRequestDto, questionTest);
        verify(questionMapper, never()).questionToQuestionResponseDto(questionTest);
    }

    @Test
    @DisplayName("Delete Question: Should delete the question and return true when it exists")
    void testDeleteQuestion() {

        given(questionRepository.findById(questionTest.getId())).willReturn(Optional.of(questionTest));

        var result = questionService.deleteQuestion(questionTest.getId());

        assertAll(() -> {
            assertNotNull(result);
            assertTrue(result);
        });

        verify(questionRepository).findById(questionTest.getId());
        verify(questionRepository).delete(questionTest);
    }

    @Test
    @DisplayName("Delete Question: Should return false when the question does not exist")
    void testDeleteQuestionFail() {

        given(questionRepository.findById(questionTest.getId())).willReturn(Optional.empty());

        var result = questionService.deleteQuestion(questionTest.getId());

        assertAll(() -> {
            assertNotNull(result);
            assertFalse(result);
        });

        verify(questionRepository).findById(questionTest.getId());
        verify(questionRepository, never()).delete(questionTest);
    }
}