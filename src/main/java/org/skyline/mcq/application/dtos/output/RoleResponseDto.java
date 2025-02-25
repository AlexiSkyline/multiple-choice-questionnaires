package org.skyline.mcq.application.dtos.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.skyline.mcq.domain.enums.TypeRole;

import java.util.UUID;

@Builder
@Getter @Setter
public class RoleResponseDto {
    private UUID id;
    private TypeRole name;
    private String description;
}
