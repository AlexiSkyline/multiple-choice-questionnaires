package org.skyline.mcq.application.mappings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.domain.models.Account;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountMapperTest {

    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    Account accountTest = Account.builder()
            .firstName("Sky")
            .lastName("Taylor")
            .username("sky_responder")
            .email("sky.taylor@example.com")
            .password("SkyPassword123")
            .profileImage("account1.jpg")
            .description("New Sky responder")
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
}