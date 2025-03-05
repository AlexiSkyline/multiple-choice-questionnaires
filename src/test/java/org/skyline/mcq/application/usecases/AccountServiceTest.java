package org.skyline.mcq.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.mappings.AccountMapper;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private Account accountTest;
    private AccountSummaryDto accountSummaryDtoTest;
    private AccountProfileUpdateDto accountProfileUpdateDtoTest;

    @BeforeEach
    void setUp() {

        accountTest = Account.builder()
                .id(UUID.randomUUID())
                .firstName("Sky")
                .lastName("Taylor")
                .username("sky_responder")
                .email("sky.taylor@example.com")
                .password("SkyPassword123")
                .profileImage("account1.jpg")
                .description("New Sky responder")
                .build();

        accountSummaryDtoTest = AccountSummaryDto.builder()
                .id(accountTest.getId())
                .firstName(accountTest.getFirstName())
                .lastName(accountTest.getLastName())
                .username(accountTest.getUsername())
                .email(accountTest.getEmail())
                .build();

        accountProfileUpdateDtoTest = AccountProfileUpdateDto.builder()
                .firstName("Update name")
                .lastName("Update lastname")
                .description("Update description")
                .build();
    }

    @Test
    @DisplayName("Save Account: Should save an account and return the response DTO when the account does not exist")
    void testSaveAccount() {

        given(accountRepository.findByUsername(accountTest.getUsername())).willReturn(Optional.empty());
        given(accountRepository.findByEmail(accountTest.getEmail())).willReturn(Optional.empty());
        given(accountRepository.save(accountTest)).willReturn(accountTest);
        given(accountMapper.accountToAccountResponseDto(accountTest)).willReturn(accountSummaryDtoTest);

        Optional<AccountSummaryDto> accountSummaryDto = accountService.saveAccount(accountTest);

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isPresent());
            assertEquals(accountSummaryDtoTest, accountSummaryDto.get());
        });

        verify(accountRepository).findByUsername(accountTest.getUsername());
        verify(accountRepository).findByEmail(accountTest.getEmail());
        verify(accountRepository).save(accountTest);
        verify(accountMapper).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Save Account: Should return empty when the account already exists by username")
    void testSaveAccountWhenUserFoundByUsername() {

        given(accountRepository.findByUsername(accountTest.getUsername())).willReturn(Optional.of(accountTest));
        given(accountRepository.findByEmail(accountTest.getEmail())).willReturn(Optional.empty());

        Optional<AccountSummaryDto> accountSummaryDto = accountService.saveAccount(accountTest);

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isEmpty());
        });

        verify(accountRepository).findByUsername(accountTest.getUsername());
        verify(accountRepository).findByEmail(accountTest.getEmail());
        verify(accountRepository, never()).save(accountTest);
        verify(accountMapper, never()).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Save Account: Should return empty when the account already exists by email")
    void testSaveAccountWhenAccountFoundByEmail() {

        given(accountRepository.findByUsername(accountTest.getUsername())).willReturn(Optional.empty());
        given(accountRepository.findByEmail(accountTest.getEmail())).willReturn(Optional.of(accountTest));

        Optional<AccountSummaryDto> accountSummaryDto = accountService.saveAccount(accountTest);

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isEmpty());
        });

        verify(accountRepository).findByUsername(accountTest.getUsername());
        verify(accountRepository).findByEmail(accountTest.getEmail());
        verify(accountRepository, never()).save(accountTest);
        verify(accountMapper, never()).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Save Account: Should return empty when the account already exists by username and email")
    void testSaveAccountWhenAccountFoundByUsernameAndEmail() {

        given(accountRepository.findByUsername(accountTest.getUsername())).willReturn(Optional.of(accountTest));
        given(accountRepository.findByEmail(accountTest.getEmail())).willReturn(Optional.of(accountTest));

        Optional<AccountSummaryDto> accountSummaryDto = accountService.saveAccount(accountTest);

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isEmpty());
        });

        verify(accountRepository).findByUsername(accountTest.getUsername());
        verify(accountRepository).findByEmail(accountTest.getEmail());
        verify(accountRepository, never()).save(accountTest);
        verify(accountMapper, never()).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Get Account by Id: Should return the account summary DTO when the account exists and is active")
    void testGetAccountById() {

        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.of(accountTest));
        given(accountMapper.accountToAccountResponseDto(accountTest)).willReturn(accountSummaryDtoTest);

        Optional<AccountSummaryDto> accountSummaryDto = accountService.getAccountById(accountTest.getId());

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isPresent());
            assertEquals(accountSummaryDtoTest, accountSummaryDto.get());
        });

        verify(accountRepository).findById(accountTest.getId());
        verify(accountMapper).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Get Account by Id: Should return empty when the account does not exist")
    void testGetAccountByIdWhenAccountNotFound() {

        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.empty());

        Optional<AccountSummaryDto> accountSummaryDto = accountService.getAccountById(accountTest.getId());

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isEmpty());
        });

        verify(accountRepository).findById(accountTest.getId());
        verify(accountMapper, never()).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Get User by Email: Should return the account summary DTO when the account exists and is active")
    void testGetUserByEmail() {

        given(accountRepository.findByEmail(accountTest.getEmail())).willReturn(Optional.of(accountTest));
        given(accountMapper.accountToAccountResponseDto(accountTest)).willReturn(accountSummaryDtoTest);

        Optional<AccountSummaryDto> accountSummaryDto = accountService.getUserByEmail(accountTest.getEmail());

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isPresent());
            assertEquals(accountSummaryDtoTest, accountSummaryDto.get());
        });

        verify(accountRepository).findByEmail(accountTest.getEmail());
        verify(accountMapper).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Get User by Email: Should return empty when the account does not exist")
    void testGetUserByEmailWhenAccountNotFound() {

        given(accountRepository.findByEmail(accountTest.getEmail())).willReturn(Optional.empty());

        Optional<AccountSummaryDto> accountSummaryDto = accountService.getUserByEmail(accountTest.getEmail());

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isEmpty());
        });

        verify(accountRepository).findByEmail(accountTest.getEmail());
        verify(accountMapper, never()).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Get User by Email: Should return empty when the account is inactive")
    void testGetUserByEmailWhenAccountInactive() {

        accountTest.setActive(false);
        given(accountRepository.findByEmail(accountTest.getEmail())).willReturn(Optional.of(accountTest));

        Optional<AccountSummaryDto> accountSummaryDto = accountService.getUserByEmail(accountTest.getEmail());

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isEmpty());
        });

        verify(accountRepository).findByEmail(accountTest.getEmail());
        verify(accountMapper, never()).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Update Account: Should update and return the account summary DTO when the account exists and is active")
    void testUpdateAccount() {

        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.of(accountTest));
        given(accountRepository.save(accountTest)).willReturn(accountTest);
        given(accountMapper.accountToAccountResponseDto(accountTest)).willReturn(accountSummaryDtoTest);

        Optional<AccountSummaryDto> accountSummaryDto = accountService.updateAccount(accountTest.getId(), accountProfileUpdateDtoTest);

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isPresent());
            assertEquals(accountSummaryDtoTest, accountSummaryDto.get());
        });

        verify(accountRepository).findById(accountTest.getId());
        verify(accountRepository).save(accountTest);
        verify(accountMapper).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Update Account: Should return empty when the account does not exist")
    void testUpdateAccountWhenAccountNotFound() {

        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.empty());

        Optional<AccountSummaryDto> accountSummaryDto = accountService.updateAccount(accountTest.getId(), accountProfileUpdateDtoTest);

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isEmpty());
        });

        verify(accountRepository).findById(accountTest.getId());
        verify(accountRepository, never()).save(accountTest);
        verify(accountMapper, never()).accountToAccountResponseDto(accountTest);
    }

    @Test
    @DisplayName("Update Account: Should return empty when the account is inactive")
    void testUpdateAccountWhenAccountInactive() {

        accountTest.setActive(false);
        given(accountRepository.findById(accountTest.getId())).willReturn(Optional.of(accountTest));

        Optional<AccountSummaryDto> accountSummaryDto = accountService.updateAccount(accountTest.getId(), accountProfileUpdateDtoTest);

        assertAll(() -> {
            assertNotNull(accountSummaryDto);
            assertTrue(accountSummaryDto.isEmpty());
        });

        verify(accountRepository).findById(accountTest.getId());
        verify(accountRepository, never()).save(accountTest);
        verify(accountMapper, never()).accountToAccountResponseDto(accountTest);
    }
}