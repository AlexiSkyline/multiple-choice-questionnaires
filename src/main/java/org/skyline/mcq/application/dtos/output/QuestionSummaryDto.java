package org.skyline.mcq.application.dtos.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter @Setter
public class QuestionSummaryDto {

    private UUID id;
    private String content;
    private String image;
    private Integer points;
    private Integer allowedAnswers;
    private String options;
    private String correctAnswers;
}

