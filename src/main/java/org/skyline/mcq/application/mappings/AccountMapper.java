package org.skyline.mcq.application.mappings;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.domain.models.Account;

@Mapper
public interface AccountMapper {

    AccountSummaryDto accountToAccountResponseDto(Account account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAccountFromAccountProfileUpdateDto(AccountProfileUpdateDto accountProfileUpdateDto, @MappingTarget Account account);
}
