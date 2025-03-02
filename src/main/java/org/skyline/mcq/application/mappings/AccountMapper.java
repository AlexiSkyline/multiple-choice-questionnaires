package org.skyline.mcq.application.mappings;

import org.mapstruct.Mapper;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.domain.models.Account;

@Mapper
public interface AccountMapper {

    AccountSummaryDto accountToAccountResponseDto(Account account);
}
