package org.skyline.mcq.infrastructure.outputport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = {"classpath:createSurvey.sql"})
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;
    private RefreshToken testRefreshToken;
    private final String testToken = "d94490ec-ccb1-4188-8739-d436d32e9fa0";

    @BeforeEach
    void setUp() {

        testAccount = accountRepository.findAll().getFirst();

        testRefreshToken = RefreshToken.builder()
                .token(testToken)
                .expiryDate(Instant.now().plus(Duration.ofDays(30)))
                .account(testAccount)
                .build();

        refreshTokenRepository.save(testRefreshToken);
    }

    @Test
    @DisplayName("Should successfully save a new refresh token")
    void testSaveRefreshToken() {

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token("new-token-123")
                .expiryDate(Instant.now().plus(Duration.ofDays(1)))
                .account(testAccount)
                .build();

        RefreshToken savedRefreshToken = refreshTokenRepository.save(newRefreshToken);

        assertAll("Verify saved refresh token properties",
                () -> assertNotNull(savedRefreshToken, "Saved token should not be null"),
                () -> assertNotNull(savedRefreshToken.getId(), "Saved token should have an ID"),
                () -> assertEquals(newRefreshToken.getToken(), savedRefreshToken.getToken(), "Tokens should match"),
                () -> assertEquals(testAccount.getId(), savedRefreshToken.getAccount().getId(), "Account IDs should match")
        );
    }

    @Test
    @DisplayName("Should find refresh token by token string when it exists")
    void testFindByToken() {

        RefreshToken foundRefreshToken = refreshTokenRepository.findByToken(testToken)
                .orElseThrow(() -> new RuntimeException("RefreshToken not found"));

        assertAll("Verify found refresh token properties",
                () -> assertNotNull(foundRefreshToken, "Token should be found"),
                () -> assertNotNull(foundRefreshToken.getId(), "Found token should have an ID"),
                () -> assertEquals(testRefreshToken.getId(), foundRefreshToken.getId(), "IDs should match"),
                () -> assertEquals(testToken, foundRefreshToken.getToken(), "Tokens should match"),
                () -> assertEquals(testRefreshToken.getExpiryDate(), foundRefreshToken.getExpiryDate(), "Expiry dates should match")
        );
    }

    @Test
    @DisplayName("Should return empty when searching for non-existent token")
    void testFindByTokenNotExist() {

        String nonExistentToken = "hjaksldjiosa";
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findByToken(nonExistentToken);

        assertTrue(foundRefreshToken.isEmpty(), "Should not find token for non-existent value");
    }

    @Test
    @DisplayName("Should find refresh token by account ID when it exists")
    void testFindByAccountId() {

        RefreshToken foundRefreshToken = refreshTokenRepository.findByAccountId(testAccount.getId())
                .orElseThrow(() -> new RuntimeException("RefreshToken not found"));

        assertAll("Verify refresh token found by account ID",
                () -> assertNotNull(foundRefreshToken, "Token should be found"),
                () -> assertEquals(testRefreshToken.getId(), foundRefreshToken.getId(), "IDs should match"),
                () -> assertEquals(testAccount.getId(), foundRefreshToken.getAccount().getId(), "Account IDs should match")
        );
    }

    @Test
    @DisplayName("Should return empty when searching by non-existent account ID")
    void testFindByAccountNotExist() {

        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findByAccountId(UUID.randomUUID());

        assertTrue(foundRefreshToken.isEmpty(), "Should not find token for non-existent account ID");
    }

    @Test
    @DisplayName("Should find refresh token by account email when it exists")
    void testFindByAccountEmail() {

        RefreshToken foundRefreshToken = refreshTokenRepository.findByAccountEmail(testAccount.getEmail())
                .orElseThrow(() -> new RuntimeException("RefreshToken not found"));

        assertAll("Verify refresh token found by account email",
                () -> assertNotNull(foundRefreshToken, "Token should be found"),
                () -> assertEquals(testRefreshToken.getId(), foundRefreshToken.getId(), "IDs should match"),
                () -> assertEquals(testAccount.getEmail(), foundRefreshToken.getAccount().getEmail(), "Emails should match")
        );
    }

    @Test
    @DisplayName("Should return empty when searching by non-existent account email")
    void testFindByAccountEmailNotExists() {

        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findByAccountEmail("nonexistent@example.com");

        assertTrue(foundRefreshToken.isEmpty(), "Should not find token for non-existent email");
    }
}