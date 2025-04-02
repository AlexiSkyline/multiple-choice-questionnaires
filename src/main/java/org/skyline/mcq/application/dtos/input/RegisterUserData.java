package org.skyline.mcq.application.dtos.input;

import lombok.*;
import org.skyline.mcq.application.dtos.output.RoleResponseDto;

import java.util.Set;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserData {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private Set<RoleResponseDto> roles;
}
