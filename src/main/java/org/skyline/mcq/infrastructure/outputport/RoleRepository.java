package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(TypeRole name);
}
