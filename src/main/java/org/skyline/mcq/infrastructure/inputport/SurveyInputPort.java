package org.skyline.mcq.infrastructure.inputport;

import org.skyline.mcq.domain.Account;
import org.skyline.mcq.domain.Survey;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface SurveyInputPort {

    Survey saveSurvey(Survey survey);
    Optional<Survey> findSurveyById(UUID id);
    Page<Survey> listSurveys(Integer pageNumber, Integer pageSize);
    Page<Survey> listSurveysByCategoryId(UUID id, Integer pageNumber, Integer pageSize);
    Page<Survey> listSurveysByStatus(Boolean status, Integer pageNumber, Integer pageSize);
    Page<Survey> listSurveysByCategoryIdAndStatus(UUID categoryId, Boolean status, Integer pageNumber, Integer pageSize);
    Page<Survey> listSurveysByHasRestrictedAccess(Boolean hasRestrictedAccess, Integer pageNumber, Integer pageSize);
    Page<Survey> listSurveysByCategoryIdAndHasRestrictedAccessAnd(UUID categoryId, Boolean hasRestrictedAccess, Integer pageNumber, Integer pageSize);
    Page<Survey> listSurveysByAccountId(UUID accountId, Integer pageNumber, Integer pageSize);
    Page<Account> listAccountsBySurveyId(UUID surveyId, Integer pageNumber, Integer pageSize);
    Optional<Survey> updateSurvey(UUID id, Survey survey);
    Boolean deleteSurvey(UUID id);
}
