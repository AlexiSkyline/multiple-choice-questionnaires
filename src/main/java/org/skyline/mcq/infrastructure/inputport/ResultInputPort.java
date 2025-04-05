package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.output.ResultResponseDto;
import org.skyline.mcq.domain.models.Result;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface ResultInputPort {

    ResultResponseDto saveResult(Result result);
    Optional<ResultResponseDto> findResultById(UUID id, UUID accountId);
    Page<ResultResponseDto> listResultBySurveyId(UUID surveyId, Integer pageNumber, Integer pageSize);
    Page<ResultResponseDto> listResultByAccountId(UUID accountId, Integer pageNumber, Integer pageSize);
    Page<ResultResponseDto> listResultBySurveyIdAndAccountId(UUID surveyId, UUID accountId, Integer pageNumber, Integer pageSize);
}
