package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.output.ResultResponseDto;
import org.skyline.mcq.application.mappings.ResultMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Result;
import org.skyline.mcq.infrastructure.inputport.ResultInputPort;
import org.skyline.mcq.infrastructure.outputport.ResultRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResultService implements ResultInputPort {

    private final ResultRepository resultRepository;
    private final ResultMapper resultMapper;
    private final PaginationHelper paginationHelper;

    @Override
    @Transactional
    public ResultResponseDto saveResult(Result result) {
        return resultMapper.resultToResultResponseDto(this.resultRepository.save(result));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResultResponseDto> listResultBySurveyId(UUID surveyId, Integer pageNumber, Integer pageSize) {
        return resultRepository.findAllByAccountId(surveyId, paginationHelper.buildPageRequest(pageNumber, pageSize))
                .map(resultMapper::resultToResultResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResultResponseDto> listResultByAccountId(UUID accountId, Integer pageNumber, Integer pageSize) {
        return resultRepository.findAllByAccountId(accountId, paginationHelper.buildPageRequest(pageNumber, pageSize))
                .map(resultMapper::resultToResultResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResultResponseDto> listResultBySurveyIdAndAccountId(UUID surveyId, UUID accountId, Integer pageNumber, Integer pageSize) {
        return resultRepository.findAllByAccountIdAndSurveyId(accountId, surveyId, paginationHelper.buildPageRequest(pageNumber, pageSize))
                .map(resultMapper::resultToResultResponseDto);
    }
}
