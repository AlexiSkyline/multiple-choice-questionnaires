package org.skyline.mcq.infrastructure.inputport;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.skyline.mcq.application.dtos.input.RefreshTokenData;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.utils.CustomUserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtInputPort {
    String extractUsername(String token);
    Date extractExpiration(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    String generateToken(String username);
    Boolean validateToken(String token, String username);
    String extractTokenFromRequest(HttpServletRequest request);
    RefreshTokenData createRefreshToken(AccountSummaryDto account);
    RefreshTokenData refreshOrCreateToken(RefreshTokenData existingToken, AccountSummaryDto account);
    CustomUserDetails getCurrentUserDetails();
}
