package org.skyline.mcq.infrastructure.outputport;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Role;
import org.skyline.mcq.infrastructure.bootstrap.BootstrapData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(BootstrapData.class)
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    Role newRole;

    @BeforeEach
    void setUp() {
        newRole = Role.builder()
                .name(TypeRole.ADMIN)
                .description("New Role")
                .build();
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Save Role: Should persist a new role and return it with an ID")
    @Transactional
    void testSaveRole() {

        var savedRole = roleRepository.save(newRole);

        assertAll(() -> {
            assertNotNull(savedRole.getId());
            assertEquals(TypeRole.ADMIN, savedRole.getName());
            assertEquals("New Role", savedRole.getDescription());
        });
    }

    @Test
    @DisplayName("Get Roles: Should return all roles from the database")
    void testGetRoles() {

        var roles = roleRepository.findAll();

        assertAll(() -> {
            assertNotNull(roles);
            assertEquals(3, roles.size());
        });
    }

    @Test
    @DisplayName("Update Role: Should update an existing role and return it with the updated data")
    @Transactional
    void testUpdateRole() {

        var foundRole = roleRepository.findAll().getLast();

        foundRole.setName(TypeRole.SURVEY_CREATOR);

        var updatedRole = roleRepository.saveAndFlush(foundRole);

        assertAll(() -> {
            assertNotNull(updatedRole.getId());
            assertEquals(TypeRole.SURVEY_CREATOR, updatedRole.getName());
            assertEquals(foundRole.getDescription(), updatedRole.getDescription());
        });
    }

    @Test
    @DisplayName("Find Role by Name: Should return a role when it exists")
    void testFindRoleByName() {

        var foundRole = roleRepository.findByName(TypeRole.ADMIN);

        assertAll(() -> {
            assertTrue(foundRole.isPresent());
            assertEquals(TypeRole.ADMIN, foundRole.get().getName());
            assertEquals("Administrator", foundRole.get().getDescription());
        });
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Find Role by Name: Should return empty when the role does not exist")
    void testFindRoleByNameNotFound() {

        roleRepository.deleteAll();
        var foundRole = roleRepository.findByName(TypeRole.SURVEY_CREATOR);

        assertTrue(foundRole.isEmpty());
    }
}