package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.RoleRequestDto;
import org.skyline.mcq.application.dtos.output.RoleResponseDto;
import org.skyline.mcq.application.mappings.RoleMapper;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Role;
import org.skyline.mcq.infrastructure.inputport.RoleInputPort;
import org.skyline.mcq.infrastructure.outputport.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleInputPort {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public Optional<RoleResponseDto> saveRole(RoleRequestDto role) {

        Optional<Role> roleFound = roleRepository.findByName(role.getName());

        if (roleFound.isEmpty()) {
            return Optional.of(roleMapper.roleToRoleResponseDto(this.roleRepository.save(roleMapper.roleRequestDtoToRole(role))));
        }

        return Optional.empty();
    }

    @Override
    public Optional<RoleResponseDto> findByName(TypeRole name) {
        return this.roleRepository.findByName(name).map(roleMapper::roleToRoleResponseDto);
    }
}
