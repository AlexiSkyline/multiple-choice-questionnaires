package org.skyline.mcq.application.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SurveyUpdateRequestDto {

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
    private Integer timeLimit;

    @NotNull
    private Integer attempts;

    @NotNull
    private Boolean hasRestrictedAccess;

    private String password;

    @NotNull
    private Boolean status;
}
