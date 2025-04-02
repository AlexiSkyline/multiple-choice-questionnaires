package org.skyline.mcq.application.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class JwtResponseDto {

    private String accessToken;
    private String refreshToken;
}