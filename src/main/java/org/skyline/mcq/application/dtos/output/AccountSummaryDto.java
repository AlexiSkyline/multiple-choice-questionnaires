package org.skyline.mcq.application.dtos.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter @Setter
public class AccountSummaryDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String description;
}
