package org.skyline.mcq.application.mappings;

import org.mapstruct.Mapper;
import org.skyline.mcq.application.dtos.input.RoleRequestDto;
import org.skyline.mcq.application.dtos.output.RoleResponseDto;
import org.skyline.mcq.domain.models.Role;

@Mapper
public interface RoleMapper {

    Role roleRequestDtoToRole(RoleRequestDto roleRequestDto);
    RoleResponseDto roleToRoleResponseDto(Role role);
}
