package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {}
