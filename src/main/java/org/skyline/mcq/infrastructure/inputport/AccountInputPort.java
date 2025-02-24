package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.domain.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountInputPort {

    Account saveAccount(Account account);
    Optional<Account> getUserByEmail(String email);
    Optional<Account> updateAccount(UUID id, Account account);
}
