package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Survey;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface SurveyInputPort {

    SurveyResponseDto saveSurvey(Survey survey);
    Optional<SurveyResponseDto> findSurveyById(UUID id);
    Page<SurveyResponseDto> listSurveys(UUID categoryId, Boolean status, Boolean hasRestrictedAccess, UUID accountId, Boolean isActive, Integer pageNumber, Integer pageSize);
    Page<Account> listAccountsBySurveyIdAndSurveyActiveAndUserActive(UUID surveyId, Boolean isActiveSurvey, Boolean isActiveAccount, Integer pageNumber, Integer pageSize);
    Optional<SurveyResponseDto> updateSurvey(UUID id, Survey survey);
    Boolean deleteSurvey(UUID id);
}
