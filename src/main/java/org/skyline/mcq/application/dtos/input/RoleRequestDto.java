package org.skyline.mcq.application.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.skyline.mcq.domain.enums.TypeRole;

@Builder
@Getter @Setter
public class RoleRequestDto {
    @NotNull
    @NotBlank
    private TypeRole name;

    @NotNull
    @NotBlank
    private String description;
}