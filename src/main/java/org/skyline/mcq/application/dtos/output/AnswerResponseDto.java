package org.skyline.mcq.application.dtos.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter @Setter
public class AnswerResponseDto {

    private UUID id;
    private QuestionResponseDto question;
    private AccountSummaryDto account;
    private ResultResponseDto result;
    private String userAnswers;
    private Boolean isCorrect;
    private Integer points;
}
