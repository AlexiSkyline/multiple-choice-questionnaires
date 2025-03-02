package org.skyline.mcq.application.dtos.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter @Setter
public class ResultResponseDto {

    private UUID id;
    private SurveyResponseDto survey;
    private AccountSummaryDto account;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer duration;
    private Integer totalPoints;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Set<AnswerResponseDto> answers;
}
