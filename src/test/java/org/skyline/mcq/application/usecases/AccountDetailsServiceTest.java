package org.skyline.mcq.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.application.utils.CustomUserDetails;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountDetailsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountDetailsService accountDetailsService;

    private Account accountTest;
    private UserDetails registerUserDataTest;

    @BeforeEach
    void setUp() {
        accountTest = Account.builder()
                .firstName("Sky")
                .lastName("Taylor")
                .username("sky_responder")
                .email("sky.taylor@example.com")
                .password("SkyPassword123")
                .profileImage("account1.jpg")
                .description("New Sky responder")
                .build();

        registerUserDataTest = CustomUserDetails.build(accountTest);
    }

    @Test
    @DisplayName("Should successfully load user details when valid email is provided")
    void testLoadUserByUsername() {
        try (MockedStatic<CustomUserDetails> mockedStatic = Mockito.mockStatic(CustomUserDetails.class)) {
            given(accountRepository.findByEmail(accountTest.getEmail()))
                    .willReturn(Optional.of(accountTest));

            mockedStatic.when(() -> CustomUserDetails.build(accountTest))
                    .thenReturn(registerUserDataTest);

            UserDetails result = accountDetailsService.loadUserByUsername(accountTest.getEmail());

            assertSame(registerUserDataTest, result,
                    "Returned UserDetails should match the mocked instance");

            mockedStatic.verify(() -> CustomUserDetails.build(accountTest));
            verify(accountRepository).findByEmail(accountTest.getEmail());
        }
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when email doesn't exist")
    void testLoadUserByUsernameWhenUserDoesNotExist() {
        given(accountRepository.findByEmail(accountTest.getEmail()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> accountDetailsService.loadUserByUsername(accountTest.getEmail()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(accountTest.getEmail());

        verify(accountRepository).findByEmail(accountTest.getEmail());

        try (MockedStatic<CustomUserDetails> mockedStatic = Mockito.mockStatic(CustomUserDetails.class)) {
            mockedStatic.verifyNoInteractions();
        }
    }
}