package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.domain.models.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountInputPort {

    Optional<AccountSummaryDto> saveAccount(Account account);
    Optional<AccountSummaryDto> getUserByEmail(String email);
    Optional<AccountSummaryDto> updateAccount(UUID id, AccountProfileUpdateDto account);
}
