package org.skyline.mcq.infrastructure.inputadapter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.SurveyAnswersDto;
import org.skyline.mcq.application.dtos.input.SurveyRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.*;
import org.skyline.mcq.domain.exceptions.ConflictException;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.ResponseHandler;
import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.skyline.mcq.infrastructure.inputport.JwtInputPort;
import org.skyline.mcq.infrastructure.inputport.SurveyInputPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SurveyAPI {

    public static final String SURVEY_PATH = "/api/v1/surveys";
    public static final String SURVEY_PATH_ID = SURVEY_PATH + "/{surveyId}";

    private final SurveyInputPort surveyInputPort;
    private final ResponseHandler responseHandler;
    private final JwtInputPort jwtInputPort;

    @PostMapping(SURVEY_PATH)
    @PreAuthorize("hasRole('SURVEY_CREATOR')")
    public ResponseEntity<ResponseBody<SurveyResponseDto>> saveSurvey(@Valid @RequestBody SurveyRequestDto surveyRequestDto) {

        UUID accountId = jwtInputPort.getCurrentUserDetails().getId();
        return surveyInputPort.saveSurvey(accountId, surveyRequestDto).map(category -> responseHandler.responseBuild(
                HttpStatus.CREATED,
                "Survey Created Successfully",
                category
        )).orElseThrow(() -> new NotFoundException("Account or Category", accountId.toString(), "Please provide a valid account or category ID"));
    }

    @GetMapping(SURVEY_PATH)
    @PreAuthorize("hasRole('SURVEY_RESPONDENT')")
    public ResponseEntity<ResponseBody<Page<SurveyResponseDto>>> getAllSurveys(@RequestParam(required = false) UUID categoryId, @RequestParam(required = false) UUID accountId,
                                                                               @RequestParam(required = false) Boolean isPublic, @RequestParam(required = false) @Positive Integer pageNumber,
                                                                               @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested All Surveys are given here",
                surveyInputPort.listSurveys(categoryId, true, isPublic, accountId, true, pageNumber, pageSize)
        );
    }

    @GetMapping(SURVEY_PATH + "/creator")
    @PreAuthorize("hasRole('SURVEY_CREATOR')")
    public ResponseEntity<ResponseBody<Page<SurveyResponseDto>>> getAllSurveys(@RequestParam(required = false) UUID categoryId, @RequestParam(required = false)  Boolean status,
                                                                               @RequestParam(required = false) Boolean isPublic, @RequestParam(required = false) UUID accountId,
                                                                               @RequestParam(required = false) @Positive Integer pageNumber, @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "(Creator) Requested All Surveys are given here",
                surveyInputPort.listSurveys(categoryId, status, isPublic, jwtInputPort.getCurrentUserDetails().getId(), true, pageNumber, pageSize)
        );
    }

    @GetMapping(SURVEY_PATH + "/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseBody<Page<SurveyResponseDto>>> getAllSurveys(@RequestParam(required = false) UUID categoryId, @RequestParam(required = false)  Boolean status,
                                                                               @RequestParam(required = false) Boolean isPublic, @RequestParam(required = false) UUID accountId,
                                                                               @RequestParam(required = false) Boolean isActive, @RequestParam(required = false) @Positive Integer pageNumber,
                                                                               @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "(Admin) Requested All Surveys are given here",
                surveyInputPort.listSurveys(categoryId, status, isPublic, accountId, isActive, pageNumber, pageSize)
        );
    }

    @GetMapping(SURVEY_PATH_ID)
    @PreAuthorize("hasAnyRole('SURVEY_RESPONDENT', 'ADMIN', 'SURVEY_CREATOR')")
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

    @GetMapping(SURVEY_PATH + "/{surveyId}/questions")
    @PreAuthorize("hasAnyRole('SURVEY_RESPONDENT', 'ADMIN', 'SURVEY_CREATOR')")
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
    @PreAuthorize("hasRole('SURVEY_CREATOR')")
    public ResponseEntity<Void> updateSurvey(@PathVariable UUID surveyId, @Valid @RequestBody SurveyUpdateRequestDto surveyRequestDto) {

        surveyInputPort.updateSurvey(surveyId, jwtInputPort.getCurrentUserDetails().getId(), surveyRequestDto).orElseThrow(() -> new NotFoundException(
                "Survey or Account",
                surveyId.toString(),
                "Please provide a valid survey or Account ID"
        ));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(SURVEY_PATH_ID)
    @PreAuthorize("hasRole('SURVEY_CREATOR')")
    public ResponseEntity<Void> deleteSurvey(@PathVariable UUID surveyId) {

        if (Boolean.FALSE.equals(surveyInputPort.deleteSurvey(surveyId, jwtInputPort.getCurrentUserDetails().getId()))) {
            throw new NotFoundException(
                    "Survey or Account",
                    surveyId.toString(),
                    "Please provide a valid survey or Account ID"
            );
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(SURVEY_PATH + "/{surveyId}/accounts")
    @PreAuthorize("hasRole('SURVEY_CREATOR')")
    public ResponseEntity<ResponseBody<Page<AccountSummaryDto>>> getSurveyAccounts(@PathVariable UUID surveyId,
                                                                                  @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                  @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "(Creator) Requested All Accounts for Survey are given here",
                surveyInputPort.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyId, true, true, pageNumber, pageSize)
        );
    }

    @GetMapping(SURVEY_PATH + "/{surveyId}/accounts/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseBody<Page<AccountSummaryDto>>> getSurveyAccounts(@PathVariable UUID surveyId,
                                                                                   @RequestParam(required = false) Boolean isActiveSurvey,
                                                                                   @RequestParam(required = false) Boolean isActiveAccounts,
                                                                                   @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                   @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "(Admin) Requested All Accounts for Survey are given here",
                surveyInputPort.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyId, isActiveSurvey, isActiveAccounts, pageNumber, pageSize)
        );
    }

    @PostMapping(SURVEY_PATH + "/submit")
    @PreAuthorize("hasRole('SURVEY_RESPONDENT')")
    public ResponseEntity<ResponseBody<ResultResponseDto>> submitSurvey(@Valid @RequestBody SurveyAnswersDto surveyAnswersDto) {
        var surveyFound = surveyInputPort.findSurveyById(surveyAnswersDto.getSurveyId()).orElseThrow(() -> new NotFoundException(
                "Survey",
                surveyAnswersDto.getSurveyId().toString(),
                "Please provide a valid survey ID"
        ));
        Timestamp startTime = Timestamp.valueOf(surveyAnswersDto.getStartTime());
        Timestamp endTime = Timestamp.valueOf(surveyAnswersDto.getEndTime());
        int durationMillis = Math.toIntExact(endTime.getTime() - startTime.getTime());
        if (durationMillis > surveyFound.getTimeLimit()) throw new ConflictException("Survey", surveyAnswersDto.getSurveyId().toString(), "Survey time limit exceeded");


        var result = surveyInputPort.submitSurvey(surveyAnswersDto, jwtInputPort.getCurrentUserDetails().getId())
                .orElseThrow(() -> new ConflictException("Survey or Account", surveyAnswersDto.getSurveyId() + " " + jwtInputPort.getCurrentUserDetails().getId(), "Survey already submitted or Account is inactive"));

        return responseHandler.responseBuild(
                HttpStatus.OK,
                "(Survey respondent) Request result for Survey are given here",
                result
        );
    }
}
