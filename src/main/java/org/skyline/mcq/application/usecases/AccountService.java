package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.mappings.AccountMapper;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.infrastructure.inputport.AccountInputPort;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountInputPort {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public Optional<AccountSummaryDto> saveAccount(Account account) {

        Optional<Account> userFoundByUsername = accountRepository.findByUsername(account.getUsername());
        Optional<Account> accountFoundByEmail = accountRepository.findByEmail((account.getEmail()));

        if (userFoundByUsername.isPresent() || accountFoundByEmail.isPresent()) return Optional.empty();

        return Optional.of(accountMapper.accountToAccountResponseDto(accountRepository.save(account)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountSummaryDto> getUserByEmail(String email) {
        return this.accountRepository.findByEmail(email)
                .filter(Account::getActive)
                .map(accountMapper::accountToAccountResponseDto);
    }

    @Override
    @Transactional
    public Optional<AccountSummaryDto> updateAccount(UUID id, AccountProfileUpdateDto account) {

        AtomicReference<Optional<AccountSummaryDto>> accountOptional = new AtomicReference<>();

        accountRepository.findById(id).ifPresentOrElse(accountFound -> {
            if (Boolean.TRUE.equals(accountFound.getActive())) {
                accountMapper.updateAccountFromAccountProfileUpdateDto(account, accountFound);
                accountOptional.set(Optional.of(accountMapper.accountToAccountResponseDto(accountRepository.save(accountFound))));
            } else {
                accountOptional.set(Optional.empty());
            }
        }, () -> accountOptional.set(Optional.empty()));

        return accountOptional.get();
    }
}
