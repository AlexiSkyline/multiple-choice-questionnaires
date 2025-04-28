package org.skyline.mcq.infrastructure.inputadapter;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.output.AnswerResponseDto;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.ResponseHandler;
import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.skyline.mcq.infrastructure.inputport.AnswerInputPort;
import org.skyline.mcq.infrastructure.inputport.JwtInputPort;
import org.skyline.mcq.infrastructure.inputport.ResultInputPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class AnswerAPI {

    private static final String ANSWER_PATH = "/api/v1/answers";

    private final AnswerInputPort answerInputPort;
    private final ResultInputPort resultInputPort;
    private final ResponseHandler responseHandler;
    private final JwtInputPort jwtInputPort;


    @GetMapping(ANSWER_PATH + "/{resultId}")
    @PreAuthorize("hasRole('SURVEY_RESPONDENT')")
    public ResponseEntity<ResponseBody<Page<AnswerResponseDto>>> listAnswerByResultId(@PathVariable UUID resultId,
                                                                                      @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                      @RequestParam(required = false) @Positive Integer pageSize) {
         resultInputPort.findResultById(resultId, jwtInputPort.getCurrentUserDetails().getId()).orElseThrow(() -> new NotFoundException(
                "Result",
                resultId.toString(),
                "Please provide a valid result ID"
        ));

        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested Answer By Result ID are given here",
                answerInputPort.listAnswerByResultId(resultId, pageNumber, pageSize)
        );
    }

}
