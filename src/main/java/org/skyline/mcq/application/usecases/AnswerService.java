package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.output.AnswerResponseDto;
import org.skyline.mcq.application.mappings.AnswerMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Answer;
import org.skyline.mcq.infrastructure.inputport.AnswerInputPort;
import org.skyline.mcq.infrastructure.outputport.AnswerRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerService implements AnswerInputPort {

    private final AnswerRepository answerRepository;
    private final PaginationHelper paginationHelper;
    private final AnswerMapper answerMapper;

    @Override
    public AnswerResponseDto saveAnswer(Answer answer) {
        return answerMapper.answerToAnswerResponseDto(answerRepository.save(answer));
    }

    @Override
    public Page<AnswerResponseDto> listAnswerByResultId(UUID resultId, Integer pageNumber, Integer pageSize) {
        return answerRepository.findAllByResultId(resultId , paginationHelper.buildPageRequest(pageNumber, pageSize))
                .map(answerMapper::answerToAnswerResponseDto);
    }
}
