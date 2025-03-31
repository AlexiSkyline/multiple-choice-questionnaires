package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skyline.mcq.application.utils.CustomUserDetails;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.infrastructure.inputport.AccountDetailsInputPort;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService, AccountDetailsInputPort {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.debug("Entering loadUserByUsername with email: {}", email);

        Account userFound = accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        log.info("User {} Found with email Successfully", userFound.getEmail());

        return CustomUserDetails.build(userFound);
    }
}
