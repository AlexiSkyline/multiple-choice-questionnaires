package org.skyline.mcq.application.dtos.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter @Setter
public class CategorySummaryDto {

    private UUID id;
    private String title;
    private String description;
    private String image;
}