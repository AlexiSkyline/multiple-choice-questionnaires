package org.skyline.mcq.infrastructure.bootstrap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.skyline.mcq.infrastructure.outputport.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BootstrapDataTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    AccountRepository accountRepository;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(roleRepository, categoryRepository, accountRepository);
    }

    @Test
    void testUpdateData() throws Exception {

        bootstrapData.run((String) null);

        assertNotEquals(0, roleRepository.count());
        assertNotEquals(0, categoryRepository.count());
        assertNotEquals(0, accountRepository.count());
    }
}