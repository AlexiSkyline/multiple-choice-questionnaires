package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.domain.models.RefreshToken;
import org.skyline.mcq.infrastructure.inputport.RefreshTokenInputPort;
import org.skyline.mcq.infrastructure.outputport.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenInputPort {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return this.refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUserEmail(String email) {
        refreshTokenRepository.findByAccountEmail(email)
                .ifPresent((refreshTokenRepository::delete));
    }

    @Override
    @Transactional
    public Boolean verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(token);
            return false;
        }

        return true;
    }
}
