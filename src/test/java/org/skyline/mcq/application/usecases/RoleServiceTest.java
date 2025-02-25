package org.skyline.mcq.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.input.RoleRequestDto;
import org.skyline.mcq.application.dtos.output.RoleResponseDto;
import org.skyline.mcq.application.mappings.RoleMapper;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Role;
import org.skyline.mcq.infrastructure.outputport.RoleRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role roleTest;
    private RoleRequestDto roleRequestDtoTest;
    private RoleResponseDto roleResponseDtoTest;

    @BeforeEach
    void setUp() {

        roleTest = Role.builder()
                .id(UUID.randomUUID())
                .name(TypeRole.SURVEY_RESPONDENT)
                .description("SURVEY_RESPONDENT")
                .build();

        roleRequestDtoTest = RoleRequestDto.builder()
                .name(TypeRole.SURVEY_RESPONDENT)
                .description("SURVEY_RESPONDENT")
                .build();

        roleResponseDtoTest = RoleResponseDto.builder()
                .id(UUID.randomUUID())
                .name(TypeRole.SURVEY_RESPONDENT)
                .description("SURVEY_RESPONDENT")
                .build();
    }

    @Test
    @DisplayName("Save Role: Should save a new role and return it")
    void testSaveRole() {

        given(roleRepository.findByName(TypeRole.SURVEY_RESPONDENT)).willReturn(Optional.empty());
        given(roleRepository.save(roleTest)).willReturn(roleTest);
        given(roleMapper.roleRequestDtoToRole(roleRequestDtoTest)).willReturn(roleTest);
        given(roleMapper.roleToRoleResponseDto(roleTest)).willReturn(roleResponseDtoTest);

        Optional<RoleResponseDto> result = roleService.saveRole(roleRequestDtoTest);

        assertAll(() -> {
            assertTrue(result.isPresent());
            assertEquals(roleResponseDtoTest, result.get());
        });

        verify(roleRepository, times(1)).findByName(TypeRole.SURVEY_RESPONDENT);
        verify(roleRepository, times(1)).save(roleTest);
        verify(roleMapper, times(1)).roleRequestDtoToRole(roleRequestDtoTest);
        verify(roleMapper, times(1)).roleToRoleResponseDto(roleTest);
    }

    @Test
    @DisplayName("Save Role: Should return empty when role already exists")
    void testSaveRoleError() {

        given(roleRepository.findByName(TypeRole.SURVEY_RESPONDENT)).willReturn(Optional.of(roleTest));

        Optional<RoleResponseDto> result = roleService.saveRole(roleRequestDtoTest);

        assertAll(() -> {
            assertFalse(result.isPresent());
        });

        verify(roleRepository, times(1)).findByName(TypeRole.SURVEY_RESPONDENT);
        verify(roleRepository, times(0)).save(roleTest);
        verify(roleMapper, times(0)).roleToRoleResponseDto(roleTest);
    }

    @Test
    @DisplayName("Find Role by Name: Should return role when it exists")
    void testFindByName() {

        given(roleRepository.findByName(TypeRole.SURVEY_RESPONDENT)).willReturn(Optional.of(roleTest));
        given(roleMapper.roleToRoleResponseDto(roleTest)).willReturn(roleResponseDtoTest);

        Optional<RoleResponseDto> result = roleService.findByName(TypeRole.SURVEY_RESPONDENT);

        assertAll(() -> {
            assertTrue(result.isPresent());
            assertEquals(roleResponseDtoTest, result.get());
        });

        verify(roleRepository, times(1)).findByName(TypeRole.SURVEY_RESPONDENT);
        verify(roleMapper, times(1)).roleToRoleResponseDto(roleTest);
    }

    @Test
    @DisplayName("Find Role by Name: Should return empty when role does not exist")
    void testFindByNameNotFound() {

        given(roleRepository.findByName(TypeRole.SURVEY_RESPONDENT)).willReturn(Optional.empty());

        Optional<RoleResponseDto> result = roleService.findByName(TypeRole.SURVEY_RESPONDENT);

        assertAll(() -> {
            assertFalse(result.isPresent());
        });

        verify(roleRepository, times(1)).findByName(TypeRole.SURVEY_RESPONDENT);
        verify(roleMapper, times(0)).roleToRoleResponseDto(roleTest);
    }
}