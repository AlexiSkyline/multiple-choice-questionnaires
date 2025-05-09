package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
}
