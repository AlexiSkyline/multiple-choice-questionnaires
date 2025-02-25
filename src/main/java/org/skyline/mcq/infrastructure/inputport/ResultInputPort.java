package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.domain.models.Result;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ResultInputPort {

    Result saveResult(Result result);
    Page<Result> listResultBySurveyId(UUID surveyId, Integer pageNumber, Integer pageSize);
    Page<Result> listResultByAccountId(UUID accountId, Integer pageNumber, Integer pageSize);
    Page<Result> listResultBySurveyIdAndAccountId(UUID surveyId, UUID accountId, Integer pageNumber, Integer pageSize);
}
