package org.skyline.mcq.infrastructure.outputport;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.models.Role;
import org.skyline.mcq.infrastructure.bootstrap.BootstrapData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(BootstrapData.class)
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Test
    @Transactional
    void testSaveRole() {

        var newRole = Role.builder()
                .name("New Role")
                .description("New Role")
                .build();

        var savedRole = roleRepository.save(newRole);

        assertNotNull(savedRole.getId());
    }

    @Test
    void testGetRoles() {

        var roles = roleRepository.findAll();

        assertNotNull(roles);
        assertEquals(3, roles.size());
    }

    @Test
    @Transactional
    void testUpdateRole() {

        var foundRole = roleRepository.findAll().getLast();

        foundRole.setName("Updated Role");

        var updatedRole = roleRepository.saveAndFlush(foundRole);

        assertNotNull(updatedRole.getId());
        assertEquals("Updated Role", updatedRole.getName());
        assertNotNull(updatedRole.getUpdatedAt());
    }

    @Test
    @Transactional
    void testDeleteRole() {

        var foundRole = roleRepository.findAll().getLast();

        foundRole.setActive(false);
        roleRepository.save(foundRole);

        assertFalse(roleRepository.findById(foundRole.getId()).get().getActive());
    }
}