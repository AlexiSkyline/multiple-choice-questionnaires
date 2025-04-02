package org.skyline.mcq.application.dtos.input;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;

import java.time.Instant;

@Builder
@Getter @Setter
public class RefreshTokenData {
    private String token;
    private Instant expiryDate;
    private AccountSummaryDto accountSummaryDto;
}
