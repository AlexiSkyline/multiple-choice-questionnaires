package org.skyline.mcq.application.mappings;

import org.mapstruct.*;
import org.skyline.mcq.application.dtos.input.AccountProfileUpdateDto;
import org.skyline.mcq.application.dtos.input.RegisterUserData;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.dtos.output.RoleResponseDto;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Role;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface AccountMapper {

    AccountSummaryDto accountToAccountResponseDto(Account account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAccountFromAccountProfileUpdateDto(AccountProfileUpdateDto accountProfileUpdateDto, @MappingTarget Account account);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleResponseDtoToRoles")
    Account registerUserDataToAccount(RegisterUserData registerUserData);

    @Named("roleResponseDtoToRoles")
    default Set<Role> roleResponseDtoToRoles(Set<RoleResponseDto> roleResponseDto) {
        if (roleResponseDto == null) {
            return Collections.emptySet();
        }

        return roleResponseDto.stream()
                .map(this::roleDtoToRole)
                .collect(Collectors.toSet());
    }

    Role roleDtoToRole(RoleResponseDto roleDto);
}
