package org.skyline.mcq.infrastructure.inputadapter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.*;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.dtos.output.JwtResponseDto;
import org.skyline.mcq.application.dtos.output.RoleResponseDto;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.exceptions.*;
import org.skyline.mcq.domain.models.RefreshToken;
import org.skyline.mcq.infrastructure.inputport.AccountInputPort;
import org.skyline.mcq.infrastructure.inputport.JwtInputPort;
import org.skyline.mcq.infrastructure.inputport.RefreshTokenInputPort;
import org.skyline.mcq.infrastructure.inputport.RoleInputPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthAPI {

    private final AccountInputPort accountInputPort;
    private final JwtInputPort jwtInputPort;
    private final RefreshTokenInputPort refreshTokenInputPort;
    private final AuthenticationManager authenticationManager;
    private final RoleInputPort roleInputPort;

    @PostMapping("/register/creator")
    public ResponseEntity<JwtResponseDto> registerCreator(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        return registerUserWithRole(signUpRequest, TypeRole.ROLE_SURVEY_CREATOR);
    }

    @PostMapping("/register/respondent")
    public ResponseEntity<JwtResponseDto> registerRespondent(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        return registerUserWithRole(signUpRequest, TypeRole.ROLE_SURVEY_RESPONDENT);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

        AccountSummaryDto account = accountInputPort.getUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Account not found"));

        String refreshToken = refreshTokenInputPort.findByAccountId(account.getId())
                .or(() -> refreshTokenInputPort.createRefreshToken(jwtInputPort.createRefreshToken(account)))
                .orElseThrow(() -> new TokenOperationException("Failed to get or create refresh token"));

        return ResponseEntity.ok(buildJwtResponse(loginRequest.getEmail(), refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequest) {
        RefreshToken validRefreshToken = validateAndGetRefreshToken(refreshTokenRequest.getRefreshToken());

        return ResponseEntity.ok(
                buildJwtResponse(validRefreshToken.getAccount().getEmail(), validRefreshToken.getToken())
        );
    }

    public void authenticateUser(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            if (!authentication.isAuthenticated()) {
                throw new InvalidCredentialsException("Invalid credentials");
            }
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Authentication failed: " + e.getMessage());
        }
    }

    public RefreshToken validateAndGetRefreshToken(String token) {
        return refreshTokenInputPort.findByToken(token)
                .filter(refreshTokenInputPort::verifyExpiration)
                .orElseThrow(() -> new TokenOperationException("Refresh token not found or expired"));
    }

    public ResponseEntity<JwtResponseDto> registerUserWithRole(SignUpRequestDto signUpRequest, TypeRole roleType) {
        RoleResponseDto role = roleInputPort.findByName(roleType)
                .orElseThrow(() -> new NotFoundException("Role", roleType.toString(), "Please provide a valid role name"));

        AccountSummaryDto createdAccount = createAccount(signUpRequest, role);

        String refreshToken = generateRefreshToken(createdAccount);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildJwtResponse(signUpRequest.getEmail(), refreshToken));
    }

    public AccountSummaryDto createAccount(SignUpRequestDto signUpRequest, RoleResponseDto role) {
        RegisterUserData registerData = buildRegisterUserData(signUpRequest, role);
        return accountInputPort.saveAccount(registerData)
                .orElseThrow(AccountAlreadyExistsException::new);
    }

    public RegisterUserData buildRegisterUserData(SignUpRequestDto signUpRequest, RoleResponseDto role) {
        return RegisterUserData.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .roles(Collections.singleton(role))
                .build();
    }

    public String generateRefreshToken(AccountSummaryDto account) {
        RefreshTokenData token = jwtInputPort.createRefreshToken(account);
        return refreshTokenInputPort.createRefreshToken(token)
                .orElseThrow(() -> new TokenGenerationException("Failed to create refresh token"));
    }

    public JwtResponseDto buildJwtResponse(String email, String refreshToken) {
        return JwtResponseDto.builder()
                .accessToken(jwtInputPort.generateToken(email))
                .refreshToken(refreshToken)
                .build();
    }
}
