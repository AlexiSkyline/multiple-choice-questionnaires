package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.RefreshTokenData;
import org.skyline.mcq.application.mappings.RefreshTokenMapper;
import org.skyline.mcq.domain.models.RefreshToken;
import org.skyline.mcq.infrastructure.inputport.RefreshTokenInputPort;
import org.skyline.mcq.infrastructure.outputport.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenInputPort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    @Transactional
    public Optional<String> createRefreshToken(RefreshTokenData refreshTokenData) {
        RefreshToken savedToken = refreshTokenRepository.save(
                refreshTokenMapper.refreshTokenDataToRefreshToken(refreshTokenData)
        );
        return Optional.of(savedToken.getToken());
    }

    @Override
    public Optional<String> findByAccountId(UUID accountId) {
        return refreshTokenRepository.findByAccountId(accountId)
                .map(RefreshToken::getToken);
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
