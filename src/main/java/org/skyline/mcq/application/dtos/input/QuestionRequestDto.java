package org.skyline.mcq.application.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter @Setter
public class QuestionRequestDto {

    @NotNull
    @NotBlank
    private String content;

    @NotNull
    @NotBlank
    private String image;

    @NotNull
    private Integer points;

    @NotNull
    private Integer allowedAnswers;

    @NotNull
    @NotBlank
    private String options;

    @NotNull
    @NotBlank
    private String correctAnswers;

    @NotNull
    private UUID surveyId;
}
