package org.skyline.mcq.application.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.skyline.mcq.application.utils.JsonValidation;

@Builder
@Getter @Setter
public class QuestionUpdateRequestDto {

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
    @JsonValidation
    private String options;

    @NotNull
    @NotBlank
    @JsonValidation
    private String correctAnswers;
}
