package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.domain.Role;

import java.util.Optional;

public interface RoleInputPort {

    Role saveRole(Role role);
    Optional<Role> findByName(String name);
}
