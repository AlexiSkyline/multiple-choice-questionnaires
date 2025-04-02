package org.skyline.mcq.application.dtos.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Builder
@Getter @Setter
public class SignUpRequestDto {

    @NonNull
    @NotBlank
    private String firstName;

    @NonNull
    @NotBlank
    private String lastName;

    @NonNull
    @NotBlank
    private String username;

    @NonNull
    @NotBlank
    private String email;

    @NonNull
    @NotBlank
    private String password;
}
