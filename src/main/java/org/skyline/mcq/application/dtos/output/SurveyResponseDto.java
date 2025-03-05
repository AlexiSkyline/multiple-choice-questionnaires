package org.skyline.mcq.application.dtos.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter @Setter
public class SurveyResponseDto {

    private UUID id;
    private String title;
    private String description;
    private String image;
    private Integer maxPoints;
    private Integer questionCount;
    private CategorySummaryDto category;
    private Integer timeLimit;
    private AccountSummaryDto account;
    private Integer attempts;
    private Boolean hasRestrictedAccess;
    private Boolean status;
    @Builder.Default
    private Set<QuestionSummaryDto> questions = new HashSet<>();
}
