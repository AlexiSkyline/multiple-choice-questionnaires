package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.input.RefreshTokenData;
import org.skyline.mcq.domain.models.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenInputPort {

    Optional<String> createRefreshToken(RefreshTokenData refreshTokenData);
    Optional<String> findByAccountId(UUID accountId);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserEmail(String email);
    Boolean verifyExpiration(RefreshToken token);
}
