package org.skyline.mcq.application.dtos.input;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDto {

    @NonNull
    @NotBlank
    private String refreshToken;
}
