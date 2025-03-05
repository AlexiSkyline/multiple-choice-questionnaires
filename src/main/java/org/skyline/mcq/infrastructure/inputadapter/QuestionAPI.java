package org.skyline.mcq.infrastructure.inputadapter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.QuestionRequestDto;
import org.skyline.mcq.application.dtos.input.QuestionUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.QuestionResponseDto;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.domain.exceptions.ConflictException;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.ResponseHandler;
import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.skyline.mcq.infrastructure.inputport.QuestionInputPort;
import org.skyline.mcq.infrastructure.inputport.SurveyInputPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class QuestionAPI {

    private static final String QUESTION_PATH = "/api/v1/questions";
    private static final String QUESTION_PATH_ID = QUESTION_PATH + "/{questionId}";

    private final SurveyInputPort surveyInputPort;
    private final QuestionInputPort questionInputPort;
    private final ResponseHandler responseHandler;

    @PostMapping(QUESTION_PATH)
    public ResponseEntity<ResponseBody<QuestionResponseDto>> saveQuestion(@Valid @RequestBody QuestionRequestDto question) {

        SurveyResponseDto surveyFound = surveyInputPort.findSurveyById(question.getSurveyId()).filter(SurveyResponseDto::getStatus)
                .orElseThrow(() -> new NotFoundException(
                        "Survey",
                        question.getSurveyId().toString(),
                        "Please provide a valid survey ID"
                ));

        if (surveyFound.getQuestionCount() == surveyFound.getQuestions().size()) {
            throw new ConflictException(
                    "Survey",
                    question.getSurveyId().toString(),
                    "The survey has reached the maximum number of questions"
            );
        }

        QuestionResponseDto questionResponseDto = questionInputPort.saveQuestion(question).orElse(null);

        return responseHandler.responseBuild(HttpStatus.CREATED, "Question Created Successfully", questionResponseDto);
    }

    @GetMapping(QUESTION_PATH_ID)
    public ResponseEntity<ResponseBody<QuestionResponseDto>> getQuestionById(@PathVariable UUID questionId) {
        return questionInputPort.findQuestionById(questionId).map(question -> responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested Question By ID are given here",
                question
        )).orElseThrow(() -> new NotFoundException(
                "Question",
                questionId.toString(),
                "Please provide a valid question ID"
        ));
    }

    @PutMapping(QUESTION_PATH_ID)
    public ResponseEntity<Void> updateQuestion(@PathVariable UUID questionId, @Valid @RequestBody QuestionUpdateRequestDto question) {

        questionInputPort.updateQuestion(questionId, question).orElseThrow(() -> new NotFoundException(
                "Question",
                questionId.toString(),
                "Please provide a valid question ID"
        ));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(QUESTION_PATH_ID)
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID questionId) {

        if (Boolean.FALSE.equals(questionInputPort.deleteQuestion(questionId))) {
            throw new NotFoundException(
                    "Question",
                    questionId.toString(),
                    "Please provide a valid question ID"
            );
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
