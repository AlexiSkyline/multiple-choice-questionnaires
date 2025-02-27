package org.skyline.mcq.application.dtos.input;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.skyline.mcq.domain.models.Account;

@Builder
@Getter @Setter
public class SurveyRequestDto {

    private String title;
    private String description;
    private String image;
    private Integer maxPoints;
    private Integer questionCount;
    private String categoryId;
    private Integer timeLimit;
    private Account account;
    private Integer attempts;
    private Boolean hasRestrictedAccess;
    private Boolean status;
}
