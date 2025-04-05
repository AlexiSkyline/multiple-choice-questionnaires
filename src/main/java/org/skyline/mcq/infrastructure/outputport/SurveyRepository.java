package org.skyline.mcq.infrastructure.outputport;

import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SurveyRepository extends JpaRepository<Survey, UUID>, JpaSpecificationExecutor<Survey> {

    Optional<Survey> findByIdAndAccountId(UUID id, UUID accountId);

    @Query("SELECT r.account FROM Result r WHERE r.survey.id = :surveyId and r.survey.active = :isActiveSurvey and r.account.active = :isActiveAccount")
    Page<Account> listAccountsBySurveyIdAndSurveyActiveAndUserActive(@Param("surveyId") UUID surveyId, @Param("isActiveSurvey") Boolean isActiveSurvey, @Param("isActiveAccount") Boolean isActiveAccount, Pageable pageable);
}
