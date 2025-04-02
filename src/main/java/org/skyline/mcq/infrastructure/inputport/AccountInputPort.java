package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.input.RegisterUserData;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;

import java.util.Optional;
import java.util.UUID;

public interface AccountInputPort {

    Optional<AccountSummaryDto> saveAccount(RegisterUserData registerUserData);
    Optional<AccountSummaryDto> getAccountById(UUID id);
    Optional<AccountSummaryDto> getUserByEmail(String email);
    Optional<AccountSummaryDto> updateAccount(UUID id, AccountProfileUpdateDto account);
}
