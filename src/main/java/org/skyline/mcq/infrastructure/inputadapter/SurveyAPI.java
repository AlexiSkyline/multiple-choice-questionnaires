package org.skyline.mcq.infrastructure.inputadapter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.SurveyRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.dtos.output.QuestionSummaryDto;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.ResponseHandler;
import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.skyline.mcq.infrastructure.inputport.SurveyInputPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SurveyAPI {

    public static final String SURVEY_PATH = "/api/v1/surveys";
    public static final String SURVEY_PATH_ID = SURVEY_PATH + "/{surveyId}";

    private final SurveyInputPort surveyInputPort;
    private final ResponseHandler responseHandler;

    @PostMapping(SURVEY_PATH)
    public ResponseEntity<ResponseBody<SurveyResponseDto>> saveSurvey(@Valid @RequestBody SurveyRequestDto surveyRequestDto) {

        return surveyInputPort.saveSurvey(surveyRequestDto).map(category -> responseHandler.responseBuild(
                HttpStatus.CREATED,
                "Survey Created Successfully",
                category
        )).orElseThrow(() -> new NotFoundException(
                "Account or Category",
                surveyRequestDto.getAccountId().toString(),
                "Please provide a valid account or category ID"
        ));
    }

    @GetMapping(SURVEY_PATH)
    public ResponseEntity<ResponseBody<Page<SurveyResponseDto>>> getAllSurveys(@RequestParam(required = false) UUID categoryId, @RequestParam(required = false) UUID accountId,
                                                                               @RequestParam(required = false) Boolean isPublic, @RequestParam(required = false) @Positive Integer pageNumber,
                                                                               @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested All Surveys are given here",
                surveyInputPort.listSurveys(categoryId, true, isPublic, accountId, true, pageNumber, pageSize)
        );
    }

    @GetMapping(SURVEY_PATH_ID)
    public ResponseEntity<ResponseBody<SurveyResponseDto>> getSurveyById(@PathVariable UUID surveyId) {

        return surveyInputPort.findSurveyById(surveyId).map(survey ->
                responseHandler.responseBuild(
                    HttpStatus.OK,
                    "Requested Survey By ID are given here",
                    survey
                )
        ).orElseThrow(() -> new NotFoundException(
                "Survey",
                surveyId.toString(),
                "Please provide a valid survey ID"
        ));
    }

    @GetMapping(SURVEY_PATH + "/questions/{surveyId}")
    public ResponseEntity<ResponseBody<Set<QuestionSummaryDto>>> getSurveyQuestions(@PathVariable UUID surveyId) {

        var survey = surveyInputPort.findSurveyById(surveyId).orElseThrow(() -> new NotFoundException(
                "Survey",
                surveyId.toString(),
                "Please provide a valid survey ID"
        ));

        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested All Questions for Survey are given here",
                survey.getQuestions()
        );
    }

    @PutMapping(SURVEY_PATH_ID)
    public ResponseEntity<Void> updateSurvey(@PathVariable UUID surveyId, @Valid @RequestBody SurveyUpdateRequestDto surveyRequestDto) {

        surveyInputPort.updateSurvey(surveyId, surveyRequestDto).orElseThrow(() -> new NotFoundException(
                "Survey",
                surveyId.toString(),
                "Please provide a valid survey ID"
        ));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(SURVEY_PATH_ID)
    public ResponseEntity<Void> deleteSurvey(@PathVariable UUID surveyId) {

        if (Boolean.FALSE.equals(surveyInputPort.deleteSurvey(surveyId))) {
            throw new NotFoundException(
                    "Survey",
                    surveyId.toString(),
                    "Please provide a valid survey ID"
            );
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(SURVEY_PATH + "/{surveyId}/accounts")
    public ResponseEntity<ResponseBody<Page<AccountSummaryDto>>> getSurveyAccounts(@PathVariable UUID surveyId,
                                                                                  @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                  @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested All Accounts for Survey are given here",
                surveyInputPort.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyId, true, true, pageNumber, pageSize)
        );
    }
}
