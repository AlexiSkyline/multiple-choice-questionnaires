package org.skyline.mcq.infrastructure.inputport;

import org.springframework.security.core.userdetails.UserDetails;

public interface AccountDetailsInputPort {

    UserDetails loadUserByUsername(String email);
}
