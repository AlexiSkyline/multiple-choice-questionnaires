package org.skyline.mcq.infrastructure.outputport;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Role;
import org.skyline.mcq.infrastructure.bootstrap.BootstrapData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(BootstrapData.class)
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    RoleRepository roleRepository;
    Role surveyRespondentRole;

    @BeforeEach
    void setUp() {
        surveyRespondentRole = roleRepository.findAll().getLast();
    }

    @Test
    @Transactional
    void testSaveAccount() {

        var newAccount = Account.builder()
                .firstName("Sky")
                .lastName("Taylor")
                .username("sky_responder")
                .email("sky.taylor@example.com")
                .password("SkyPassword123")
                .profileImage("account1.jpg")
                .description("New Sky responder")
                .build();
        newAccount.getRoles().add(surveyRespondentRole);

        var accountSaved = accountRepository.save(newAccount);

        assertNotNull(accountSaved.getId());
        assertEquals(newAccount.getUsername(), accountSaved.getUsername());
        assertEquals(newAccount.getDescription(), accountSaved.getDescription());
        assertNotNull(newAccount.getRoles());
        assertEquals(surveyRespondentRole.getId(), newAccount.getRoles().stream().findFirst().get().getId());
    }

    @Test
    void testGetAccounts() {

        var accounts = accountRepository.findAll();

        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
        assertEquals(3, accounts.size());
    }


    @Test
    @Transactional
    void testUpdateAccount() {

        var newAccount = Account.builder()
                .firstName("new account")
                .lastName("new account")
                .username("new_ac_responder")
                .email("new.account@example.com")
                .password("NewPassword123")
                .profileImage("new.account1.jpg")
                .description("New Account responder")
                .build();
        newAccount.getRoles().add(surveyRespondentRole);

        var accountSaved = accountRepository.save(newAccount);

        accountSaved.setFirstName("update account");
        accountSaved.setLastName("update account");
        accountSaved.setDescription("update account");

        var updatedAccount = accountRepository.saveAndFlush(accountSaved);

        assertAll(() -> {
            assertNotEquals("new account", updatedAccount.getFirstName());
            assertNotEquals("new account", updatedAccount.getLastName());
            assertNotEquals("New Account responder", updatedAccount.getDescription());
            assertNotNull(updatedAccount.getUpdatedAt());
        });
    }

    @Test
    @Transactional
    void testDeleteAccount() {

        var newAccount = Account.builder()
                .firstName("account to delete")
                .lastName("account to delete")
                .username("ac_responder_delete")
                .email("delete_account@example.com")
                .password("DeletePassword123")
                .profileImage("delete.account1.jpg")
                .description("Delete Account responder")
                .build();

        var accountSaved = accountRepository.save(newAccount);

        accountRepository.delete(accountSaved);

        assertFalse(accountRepository.findById(accountSaved.getId()).isPresent());
    }
}