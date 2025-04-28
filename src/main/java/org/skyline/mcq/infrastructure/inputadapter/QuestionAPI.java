package org.skyline.mcq.infrastructure.inputadapter;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.skyline.mcq.infrastructure.utils.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'SURVEY_CREATOR')")
public class QuestionAPI {

    private static final String QUESTION_PATH = "/api/v1/questions";
    private static final String QUESTION_PATH_ID = QUESTION_PATH + "/{questionId}";

    private final SurveyInputPort surveyInputPort;
    private final QuestionInputPort questionInputPort;
    private final ResponseHandler responseHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(QUESTION_PATH)
    public ResponseEntity<ResponseBody<QuestionResponseDto>> saveQuestion(@Valid @RequestBody QuestionRequestDto question) {

        SurveyResponseDto surveyFound = surveyInputPort
                .findSurveyByIdAndAccountId(question.getSurveyId(),jwtTokenProvider.getCurrentUserDetails().getId())
                .filter(SurveyResponseDto::getStatus)
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

        QuestionResponseDto questionResponseDto = questionInputPort.saveQuestion(question)
                .orElseThrow(() -> new ConflictException(
                        "Question",
                        question.getSurveyId().toString(),
                        "Could not save the question"
                ));

        return responseHandler.responseBuild(HttpStatus.CREATED, "Question Created Successfully", questionResponseDto);
    }

    @GetMapping(QUESTION_PATH_ID)
    public ResponseEntity<ResponseBody<QuestionResponseDto>> getQuestionById(@PathVariable UUID questionId) {

        return questionInputPort.findQuestionByIdAndAccountId(questionId, jwtTokenProvider.getCurrentUserDetails().getId())
                .map(question -> responseHandler.responseBuild(
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

        UUID accountId = jwtTokenProvider.getCurrentUserDetails().getId();
        questionInputPort.findQuestionByIdAndAccountId(questionId, accountId).orElseThrow(() -> new NotFoundException(
                "Question",
                questionId.toString(),
                "Please provide a valid question ID"
        ));

        questionInputPort.updateQuestion(questionId, question);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(QUESTION_PATH_ID)
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID questionId) {

        UUID accountId = jwtTokenProvider.getCurrentUserDetails().getId();
        if (Boolean.FALSE.equals(questionInputPort.deleteQuestion(questionId, accountId))) {
            throw new NotFoundException(
                    "Question",
                    questionId.toString(),
                    "Please provide a valid question ID"
            );
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
