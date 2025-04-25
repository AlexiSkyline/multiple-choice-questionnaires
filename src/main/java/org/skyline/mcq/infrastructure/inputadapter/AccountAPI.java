package org.skyline.mcq.infrastructure.inputadapter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.ResponseHandler;
import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.skyline.mcq.infrastructure.inputport.AccountInputPort;
import org.skyline.mcq.infrastructure.utils.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SURVEY_CREATOR', 'SURVEY_RESPONDENT')")
public class AccountAPI {

    private static final String ACCOUNT_PATH = "/api/v1/accounts";

    private final AccountInputPort accountInputPort;
    private final ResponseHandler responseHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping(ACCOUNT_PATH)
    public ResponseEntity<ResponseBody<AccountSummaryDto>> getProfile() {

        UUID accountId = jwtTokenProvider.getCurrentUserDetails().getId();
        return accountInputPort.getAccountById(accountId).map(accountSummaryDto ->
                responseHandler.responseBuild(HttpStatus.OK, "Get account Successfully", accountSummaryDto))
                .orElseThrow(() -> new NotFoundException(
                    "Account",
                    accountId.toString(),
                    "Please provide a valid question ID"
                ));
    }

    @PutMapping(ACCOUNT_PATH)
    public ResponseEntity<ResponseBody<AccountSummaryDto>> updateAccountInfo(@Valid @RequestBody AccountProfileUpdateDto accountProfileUpdateDto) {

        UUID accountId = jwtTokenProvider.getCurrentUserDetails().getId();
        return accountInputPort.updateAccount(accountId, accountProfileUpdateDto).map(accountSummaryDto ->
                        responseHandler.responseBuild(HttpStatus.OK, "Account Updated Successfully", accountSummaryDto)
                ).orElseThrow(() -> new NotFoundException(
                        "Account",
                        accountId.toString(),
                        "Please provide a valid question ID"
                ));
    }
}
