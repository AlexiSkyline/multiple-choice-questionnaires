package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByAccountId(UUID id);
    Optional<RefreshToken> findByAccountEmail(String email);
}
