package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.domain.models.RefreshToken;

import java.util.Optional;

public interface RefreshTokenInputPort {

    RefreshToken createRefreshToken(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserEmail(String email);
    Boolean verifyExpiration(RefreshToken token);
}
