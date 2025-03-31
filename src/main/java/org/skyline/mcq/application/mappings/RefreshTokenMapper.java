package org.skyline.mcq.application.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.skyline.mcq.application.dtos.input.RefreshTokenData;
import org.skyline.mcq.domain.models.RefreshToken;

@Mapper
public interface RefreshTokenMapper {

    @Mapping(target = "account", source = "accountSummaryDto")
    RefreshToken refreshTokenDataToRefreshToken(RefreshTokenData refreshTokenData);
}
