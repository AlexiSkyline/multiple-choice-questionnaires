package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.input.RoleRequestDto;
import org.skyline.mcq.application.dtos.output.RoleResponseDto;
import org.skyline.mcq.domain.enums.TypeRole;

import java.util.Optional;

public interface RoleInputPort {

    Optional<RoleResponseDto> saveRole(RoleRequestDto role);
    Optional<RoleResponseDto> findByName(TypeRole name);
}
