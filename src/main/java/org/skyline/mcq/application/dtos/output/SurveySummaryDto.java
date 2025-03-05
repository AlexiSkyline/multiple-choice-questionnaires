package org.skyline.mcq.application.dtos.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter @Setter
public class SurveySummaryDto {

    private UUID id;
    private String title;
    private String description;
    private String image;
    private Integer maxPoints;
    private Integer questionCount;
    private Integer timeLimit;
    private Integer attempts;
    private Boolean hasRestrictedAccess;
    private Boolean status;
}
