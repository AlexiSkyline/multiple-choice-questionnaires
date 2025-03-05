package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.input.SurveyRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface SurveyInputPort {

    Optional<SurveyResponseDto> saveSurvey(SurveyRequestDto survey);
    Optional<SurveyResponseDto> findSurveyById(UUID id);
    Page<SurveyResponseDto> listSurveys(UUID categoryId, Boolean status, Boolean hasRestrictedAccess, UUID accountId, Boolean isActive, Integer pageNumber, Integer pageSize);
    Page<AccountSummaryDto> listAccountsBySurveyIdAndSurveyActiveAndUserActive(UUID surveyId, Boolean isActiveSurvey, Boolean isActiveAccount, Integer pageNumber, Integer pageSize);
    Optional<SurveyResponseDto> updateSurvey(UUID id, SurveyUpdateRequestDto survey);
    Boolean deleteSurvey(UUID id);
}
