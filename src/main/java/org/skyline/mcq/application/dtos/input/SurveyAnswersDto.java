package org.skyline.mcq.application.dtos.input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter @Setter
public class SurveyAnswersDto {

    @NonNull
    private UUID surveyId;

    @NonNull
    private List<AnswersDto> answers;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;
}
