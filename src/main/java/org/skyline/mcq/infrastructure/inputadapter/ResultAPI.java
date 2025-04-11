package org.skyline.mcq.infrastructure.inputadapter;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.output.ResultResponseDto;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.ResponseHandler;
import org.skyline.mcq.infrastructure.inputport.AccountInputPort;
import org.skyline.mcq.infrastructure.inputport.JwtInputPort;
import org.skyline.mcq.infrastructure.inputport.ResultInputPort;
import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.skyline.mcq.infrastructure.inputport.SurveyInputPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ResultAPI {

    public static final String RESULT_PATH = "/api/v1/results";
    public static final String RESULT_PATH_ID = RESULT_PATH + "/{resultId}";

    private final SurveyInputPort surveyInputPort;
    private final AccountInputPort accountInputPort;
    private final ResultInputPort resultInputPort;
    private final JwtInputPort jwtInputPort;
    private final ResponseHandler responseHandler;

    @GetMapping(RESULT_PATH_ID)
    @PreAuthorize("hasRole('SURVEY_RESPONDENT')")
    public ResponseEntity<ResponseBody<ResultResponseDto>> getResultById(@PathVariable UUID resultId) {
        UUID accountId = jwtInputPort.getCurrentUserDetails().getId();
        validateAccountExists(accountId);

        return resultInputPort.findResultById(resultId, accountId)
                .filter(resultResponseDto ->
                    resultResponseDto.getAccount().getId().equals(accountId)
                ).map(result -> responseHandler.responseBuild(
                        HttpStatus.OK,
                        "Requested Result is given here",
                        result
                )).orElseThrow(() -> new NotFoundException("Result", resultId.toString(), "Please provide a valid result ID"));
    }

    @GetMapping(RESULT_PATH + "/survey/{surveyId}")
    @PreAuthorize("hasRole('ROLE_SURVEY_CREATOR')")
    public ResponseEntity<ResponseBody<Page<ResultResponseDto>>> getResultBySurveyId(@PathVariable UUID surveyId,
                                                                                     @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                     @RequestParam(required = false) @Positive Integer pageSize) {

        surveyInputPort.findSurveyById(surveyId)
                .filter(surveyResponseDto ->
                        surveyResponseDto.getAccount().getId().equals(jwtInputPort.getCurrentUserDetails().getId())
                )
                .orElseThrow(() -> new NotFoundException("Survey", surveyId.toString(), "Please provide a valid survey ID"));

        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested Result is given here",
                resultInputPort.listResultBySurveyId(surveyId, pageNumber, pageSize)
        );
    }

    @GetMapping(RESULT_PATH + "/account")
    @PreAuthorize("hasRole('SURVEY_RESPONDENT')")
    public ResponseEntity<ResponseBody<Page<ResultResponseDto>>> getAllResultByAccountId(@RequestParam(required = false) @Positive Integer pageNumber,
                                                                                      @RequestParam(required = false) @Positive Integer pageSize) {

        UUID accountId = jwtInputPort.getCurrentUserDetails().getId();
        validateAccountExists(accountId);

        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested Result is given here",
                resultInputPort.listResultByAccountId(accountId, pageNumber, pageSize)
        );
    }

    @GetMapping(RESULT_PATH + "/survey/{surveyId}/account/{accountId}")
    @PreAuthorize("hasRole('SURVEY_RESPONDENT')")
    public ResponseEntity<ResponseBody<Page<ResultResponseDto>>> getResultBySurveyIdAndAccountId(@PathVariable UUID surveyId, @PathVariable UUID accountId,
                                                                                                 @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                                 @RequestParam(required = false) @Positive Integer pageSize) {

        validateAccountExists(accountId);

        surveyInputPort.findSurveyById(surveyId)
                .orElseThrow(() -> new NotFoundException("Survey", surveyId.toString(), "Please provide a valid survey ID"));

        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested Result is given here",
                resultInputPort.listResultBySurveyIdAndAccountId(surveyId, accountId, pageNumber, pageSize)
        );
    }

    public void validateAccountExists(UUID accountId) {
        accountInputPort.getAccountById(accountId).orElseThrow(() -> new NotFoundException(
                "Account",
                accountId.toString(),
                "Please provide a valid account ID"
        ));
    }
}
