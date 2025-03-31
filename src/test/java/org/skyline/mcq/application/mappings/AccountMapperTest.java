package org.skyline.mcq.application.mappings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.input.RegisterUserData;
import org.skyline.mcq.application.dtos.output.RoleResponseDto;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Role;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountMapperTest {

    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    Role roleTest = Role.builder()
            .id(UUID.randomUUID())
            .name(TypeRole.ROLE_ADMIN)
            .description("Admin")
            .build();

    RoleResponseDto roleResponseDto = RoleResponseDto.builder()
            .id(UUID.randomUUID())
            .name(TypeRole.ROLE_ADMIN)
            .description("Admin")
            .build();

    Account accountTest = Account.builder()
            .firstName("Sky")
            .lastName("Taylor")
            .username("sky_responder")
            .email("sky.taylor@example.com")
            .password("SkyPassword123")
            .profileImage("account1.jpg")
            .description("New Sky responder")
            .roles(Collections.singleton(roleTest))
            .build();

    @Test
    @DisplayName("Account to AccountResponseDto: Should map account to account response DTO correctly")
    void accountToAccountResponseDto() {

        var result = accountMapper.accountToAccountResponseDto(accountTest);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(accountTest.getFirstName(), result.getFirstName());
            assertEquals(accountTest.getLastName(), result.getLastName());
            assertEquals(accountTest.getUsername(), result.getUsername());
            assertEquals(accountTest.getEmail(), result.getEmail());
        });
    }

    @Test
    @DisplayName("Update Account from AccountProfileUpdateDto: Should update account fields from profile update DTO")
    void updateAccountFromAccountProfileUpdateDto() {

        var accountProfileUpdateDto = AccountProfileUpdateDto.builder()
                .firstName("Update firstName")
                .lastName("Update lastName")
                .description("Update description")
                .build();

        accountMapper.updateAccountFromAccountProfileUpdateDto(accountProfileUpdateDto, accountTest);

        assertAll(() -> {
            assertEquals(accountProfileUpdateDto.getFirstName(), accountTest.getFirstName());
            assertEquals(accountProfileUpdateDto.getLastName(), accountTest.getLastName());
            assertEquals(accountProfileUpdateDto.getDescription(), accountTest.getDescription());
        });
    }

    @Test
    @DisplayName("RegisterUserData to Account: Should map all fields correctly including roles")
    void registerUserDataToAccount() {
        RegisterUserData registerUserData = RegisterUserData.builder()
                .firstName("Sky")
                .lastName("Taylor")
                .username("sky_responder")
                .email("sky.taylor@example.com")
                .password("SkyPassword123")
                .roles(Collections.singleton(roleResponseDto))
                .build();

        var result = accountMapper.registerUserDataToAccount(registerUserData);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(registerUserData.getFirstName(), result.getFirstName());
            assertEquals(registerUserData.getLastName(), result.getLastName());
            assertEquals(registerUserData.getUsername(), result.getUsername());
            assertEquals(registerUserData.getEmail(), result.getEmail());
            assertFalse(result.getRoles().isEmpty());
        });
    }

    @Test
    @DisplayName("RoleResponseDto Set to Roles Set: Should convert set of DTOs to set of entities")
    void roleResponseDtoToRoles() {
        var result = accountMapper.roleResponseDtoToRoles(Collections.singleton(roleResponseDto));

        assertAll(() -> {
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        });
    }

    @Test
    @DisplayName("RoleDto to Role: Should map individual role DTO to role entity with all fields")
    void roleDtoToRole() {
        var result = accountMapper.roleDtoToRole(roleResponseDto);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(result.getId(), roleResponseDto.getId());
            assertEquals(result.getName(), roleResponseDto.getName());
            assertEquals(result.getDescription(), roleResponseDto.getDescription());
        });
    }
}