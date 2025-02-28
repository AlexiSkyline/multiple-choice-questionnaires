package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.QuestionRequestDto;
import org.skyline.mcq.application.dtos.input.QuestionUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.QuestionResponseDto;
import org.skyline.mcq.application.mappings.QuestionMapper;
import org.skyline.mcq.domain.models.Question;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.infrastructure.inputport.QuestionInputPort;
import org.skyline.mcq.infrastructure.outputport.QuestionRepository;
import org.skyline.mcq.infrastructure.outputport.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService implements QuestionInputPort {

    private final QuestionRepository questionRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionMapper questionMapper;


    @Override
    @Transactional
    public Optional<QuestionResponseDto> saveQuestion(QuestionRequestDto question) {

        Optional<Survey> survey = surveyRepository.findById(question.getSurveyId()).filter(Survey::getActive);

        if (survey.isEmpty()) return Optional.empty();

        Question newQuestion = questionMapper.questionRequestDtoToQuestion(question);
        newQuestion.setSurvey(survey.get());

        return Optional.of(questionMapper.questionToQuestionResponseDto(questionRepository.save(newQuestion)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionResponseDto> findQuestionById(UUID id) {
        return Optional.ofNullable(questionMapper.questionToQuestionResponseDto(questionRepository.findById(id).orElse(null)));
    }

    @Override
    @Transactional
    public Optional<QuestionResponseDto> updateQuestion(UUID uuid, QuestionUpdateRequestDto question) {

        return questionRepository.findById(uuid).map(questionFound -> {
            questionMapper.updateQuestionFromQuestionRequestDto(question, questionFound);
            return questionMapper.questionToQuestionResponseDto(questionRepository.save(questionFound));
        });
    }

    @Override
    @Transactional
    public Boolean deleteQuestion(UUID id) {

        return questionRepository.findById(id).map(questionFound -> {
            questionRepository.delete(questionFound);
            return true;
        }).orElse(false);
    }
}
