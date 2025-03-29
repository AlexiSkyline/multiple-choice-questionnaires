package org.skyline.mcq.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.RefreshToken;
import org.skyline.mcq.infrastructure.outputport.RefreshTokenRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private RefreshToken refreshTokenTest;
    private RefreshToken expiredRefreshTokenTest;
    private Account accountTest;

    @BeforeEach
    void setUp() {
        accountTest = Account.builder()
                .id(UUID.randomUUID())
                .firstName("Ethan")
                .lastName("Miller")
                .username("ethan_creator")
                .email("ethan.miller@example.com")
                .password("EthanPassword123")
                .profileImage("creator2.jpg")
                .description("Poll Maker")
                .build();

        refreshTokenTest = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token("valid-token-123")
                .expiryDate(Instant.now().plus(Duration.ofDays(30)))
                .account(accountTest)
                .build();

        expiredRefreshTokenTest = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token("expired-token-456")
                .expiryDate(Instant.now().minus(Duration.ofDays(1)))
                .account(accountTest)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a refresh token")
    void testCreateRefreshToken() {

        given(refreshTokenRepository.save(refreshTokenTest)).willReturn(refreshTokenTest);

        var result = refreshTokenService.createRefreshToken(refreshTokenTest);

        assertAll("Verify created refresh token",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(refreshTokenTest.getId(), result.getId(), "IDs should match")
        );

        verify(refreshTokenRepository).save(refreshTokenTest);
    }

    @Test
    @DisplayName("Should find refresh token when token exists")
    void testFindByToken() {

        given(refreshTokenRepository.findByToken(refreshTokenTest.getToken()))
                .willReturn(Optional.of(refreshTokenTest));

        var result = refreshTokenService.findByToken(refreshTokenTest.getToken());

        assertAll("Verify found refresh token",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertTrue(result.isPresent(), "Token should be present")
        );

        verify(refreshTokenRepository).findByToken(refreshTokenTest.getToken());
    }

    @Test
    @DisplayName("Should return empty when token doesn't exist")
    void testFindByTokenNotExist() {

        given(refreshTokenRepository.findByToken(refreshTokenTest.getToken()))
                .willReturn(Optional.empty());

        var result = refreshTokenService.findByToken(refreshTokenTest.getToken());

        assertTrue(result.isEmpty(), "Result should be empty for non-existent token");
        verify(refreshTokenRepository).findByToken(refreshTokenTest.getToken());
    }

    @Test
    @DisplayName("Should delete refresh token when user email exists")
    void testDeleteByUserEmail() {

        given(refreshTokenRepository.findByAccountEmail(accountTest.getEmail()))
                .willReturn(Optional.of(refreshTokenTest));

        refreshTokenService.deleteByUserEmail(accountTest.getEmail());

        verify(refreshTokenRepository).findByAccountEmail(accountTest.getEmail());
        verify(refreshTokenRepository).delete(refreshTokenTest);
    }

    @Test
    @DisplayName("Should not delete when user email doesn't exist")
    void testDeleteByUserEmailNotExist() {

        given(refreshTokenRepository.findByAccountEmail(accountTest.getEmail()))
                .willReturn(Optional.empty());

        refreshTokenService.deleteByUserEmail(accountTest.getEmail());

        verify(refreshTokenRepository).findByAccountEmail(accountTest.getEmail());
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should verify valid token expiration")
    void testVerifyExpiration() {

        var result = refreshTokenService.verifyExpiration(refreshTokenTest);

        assertTrue(result, "Should return true for valid token");
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should fail verification for expired token")
    void testVerifyExpirationTokenExpired() {

        var result = refreshTokenService.verifyExpiration(expiredRefreshTokenTest);

        assertAll("Verify expired token handling",
                () -> assertFalse(result, "Should return false for expired token"),
                () -> verify(refreshTokenRepository).delete(expiredRefreshTokenTest)
        );
    }
}