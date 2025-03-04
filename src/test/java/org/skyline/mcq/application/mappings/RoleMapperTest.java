package org.skyline.mcq.application.mappings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.input.RoleRequestDto;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Role;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoleMapperTest {

    private final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    Role roleTest;

    @BeforeEach
    void setUp() {

        roleTest = Role.builder()
                .id(UUID.randomUUID())
                .name(TypeRole.ADMIN)
                .description("Admin")
                .build();
    }

    @Test
    @DisplayName("RoleRequestDto to Role: Should map role request DTO to role correctly")
    void roleRequestDtoToRole() {

        var roleRequestDto = RoleRequestDto.builder()
                .name(TypeRole.ADMIN)
                .description("Admin")
                .build();

        var role = roleMapper.roleRequestDtoToRole(roleRequestDto);

        assertAll(() -> {
            assertNotNull(role);
            assertEquals(roleRequestDto.getName(), role.getName());
            assertEquals(roleRequestDto.getDescription(), role.getDescription());
        });
    }

    @Test
    @DisplayName("Role to RoleResponseDto: Should map role to role response DTO correctly")
    void roleToRoleResponseDto() {

        var roleResponseDto = roleMapper.roleToRoleResponseDto(roleTest);

        assertAll(() -> {
            assertNotNull(roleResponseDto);
            assertEquals(roleTest.getName(), roleResponseDto.getName());
            assertEquals(roleTest.getDescription(), roleResponseDto.getDescription());
        });
    }
}