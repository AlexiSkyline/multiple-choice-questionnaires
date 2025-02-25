package org.skyline.mcq.application.dtos.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.skyline.mcq.domain.models.Account;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Getter @Setter
public class CategoryResponseDto {

    private UUID id;
    private String title;
    private String description;
    private String image;
    private Account account;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
