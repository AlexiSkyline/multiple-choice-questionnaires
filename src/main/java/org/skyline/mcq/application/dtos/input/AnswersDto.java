package org.skyline.mcq.application.dtos.input;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.skyline.mcq.application.utils.JsonValidation;

import java.util.UUID;

@Builder
@Getter @Setter
public class AnswersDto {

    @NotNull
    private UUID questionId;

    @NotNull
    @NotEmpty
    @JsonValidation
    private String userAnswers;
}
