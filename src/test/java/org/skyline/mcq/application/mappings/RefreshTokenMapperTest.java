package org.skyline.mcq.application.mappings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.skyline.mcq.application.dtos.input.RefreshTokenData;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.RefreshToken;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenMapperTest {

    private final RefreshTokenMapper refreshTokenMapper = Mappers.getMapper(RefreshTokenMapper.class);

    private RefreshToken refreshTokenTest;
    private RefreshTokenData refreshTokenDataTest;

    @BeforeEach
    void setUp() {

        Account accountTest = Account.builder()
                .id(UUID.randomUUID())
                .firstName("Ethan")
                .lastName("Miller")
                .username("ethan_creator")
                .email("ethan.miller@example.com")
                .password("EthanPassword123")
                .profileImage("creator2.jpg")
                .description("Poll Maker")
                .build();

        AccountSummaryDto accountSummaryTest = AccountSummaryDto.builder()
                .id(UUID.randomUUID())
                .firstName("Ethan")
                .lastName("Miller")
                .username("ethan_creator")
                .email("ethan.miller@example.com")
                .build();

        refreshTokenTest = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now())
                .account(accountTest)
                .build();

        refreshTokenDataTest = RefreshTokenData.builder()
                .token(refreshTokenTest.getToken())
                .expiryDate(refreshTokenTest.getExpiryDate())
                .accountSummaryDto(accountSummaryTest)
                .build();
    }

    @Test
    @DisplayName("RefreshTokenData to RefreshToken: Should map RefreshTokenData DTO to RefreshToken correctly")
    void refreshTokenDataToRefreshToken() {
        var result = refreshTokenMapper.refreshTokenDataToRefreshToken(refreshTokenDataTest);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(result.getToken(), refreshTokenTest.getToken());
            assertEquals(result.getExpiryDate(), refreshTokenDataTest.getExpiryDate());
        });
    }
}