package org.skyline.mcq.infrastructure.inputadapter;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.output.ResultResponseDto;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.ResponseHandler;
import org.skyline.mcq.infrastructure.inputport.AccountInputPort;
import org.skyline.mcq.infrastructure.inputport.ResultInputPort;
import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.skyline.mcq.infrastructure.inputport.SurveyInputPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ResponseHandler responseHandler;

    @GetMapping(RESULT_PATH_ID)
    public ResponseEntity<ResponseBody<ResultResponseDto>> getResultById(@PathVariable UUID resultId) {
        return resultInputPort.findResultById(resultId).map(result -> responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested Result is given here",
                result
        )).orElseThrow(() -> new NotFoundException(
                "Result",
                resultId.toString(),
                "Please provide a valid result ID"
        ));
    }

    @GetMapping(RESULT_PATH + "/survey/{surveyId}")
    public ResponseEntity<ResponseBody<Page<ResultResponseDto>>> getResultBySurveyId(@PathVariable UUID surveyId,
                                                                                     @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                     @RequestParam(required = false) @Positive Integer pageSize) {

        surveyInputPort.findSurveyById(surveyId).orElseThrow(() -> new NotFoundException(
                "Survey",
                surveyId.toString(),
                "Please provide a valid survey ID"
        ));

        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested Result is given here",
                resultInputPort.listResultBySurveyId(surveyId, pageNumber, pageSize)
        );
    }

    @GetMapping(RESULT_PATH + "/account/{accountId}")
    public ResponseEntity<ResponseBody<Page<ResultResponseDto>>> getResultByAccountId(@PathVariable UUID accountId,
                                                                                      @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                      @RequestParam(required = false) @Positive Integer pageSize) {

        accountInputPort.getAccountById(accountId).orElseThrow(() -> new NotFoundException(
                "Account",
                accountId.toString(),
                "Please provide a valid account ID"
        ));

        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested Result is given here",
                resultInputPort.listResultByAccountId(accountId, pageNumber, pageSize)
        );
    }

    @GetMapping(RESULT_PATH + "/survey/{surveyId}/account/{accountId}")
    public ResponseEntity<ResponseBody<Page<ResultResponseDto>>> getResultBySurveyIdAndAccountId(@PathVariable UUID surveyId, @PathVariable UUID accountId,
                                                                                                 @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                                 @RequestParam(required = false) @Positive Integer pageSize) {

        surveyInputPort.findSurveyById(surveyId).orElseThrow(() -> new NotFoundException(
                "Survey",
                surveyId.toString(),
                "Please provide a valid survey ID"
        ));

        accountInputPort.getAccountById(accountId).orElseThrow(() -> new NotFoundException(
                "Account",
                accountId.toString(),
                "Please provide a valid account ID"
        ));

        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested Result is given here",
                resultInputPort.listResultBySurveyIdAndAccountId(surveyId, accountId, pageNumber, pageSize)
        );
    }
}
