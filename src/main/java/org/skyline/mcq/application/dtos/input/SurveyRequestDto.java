package org.skyline.mcq.application.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter @Setter
public class SurveyRequestDto {

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String description;

    private String image;

    @NotNull
    private Integer maxPoints;

    @NotNull
    private Integer questionCount;

    @NotNull
    private UUID categoryId;

    @NotNull
    private Integer timeLimit;

    @NotNull
    private Integer attempts;

    @NotNull
    private Boolean hasRestrictedAccess;

    private String password;

    @NotNull
    private Boolean status;
}
