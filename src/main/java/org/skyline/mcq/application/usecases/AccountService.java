package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.input.RegisterUserData;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.mappings.AccountMapper;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.infrastructure.inputport.AccountInputPort;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountInputPort {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public Optional<AccountSummaryDto> saveAccount(RegisterUserData registerUserData) {

        Optional<Account> userFoundByUsername = accountRepository.findByUsername(registerUserData.getUsername());
        Optional<Account> accountFoundByEmail = accountRepository.findByEmail((registerUserData.getEmail()));

        if (userFoundByUsername.isPresent() || accountFoundByEmail.isPresent()) return Optional.empty();

        registerUserData.setPassword(this.passwordEncoder.encode(registerUserData.getPassword()));
        Account newAccount = this.accountMapper.registerUserDataToAccount(registerUserData);

        return Optional.of(
            accountMapper.accountToAccountResponseDto(
                accountRepository.save(newAccount)
            )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountSummaryDto> getAccountById(UUID id) {
        return this.accountRepository.findById(id)
                .filter(Account::getActive)
                .map(accountMapper::accountToAccountResponseDto);
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
