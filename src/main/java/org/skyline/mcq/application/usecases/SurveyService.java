package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.application.mappings.SurveyMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.domain.specification.SurveySpecifications;
import org.skyline.mcq.infrastructure.inputport.SurveyInputPort;
import org.skyline.mcq.infrastructure.outputport.SurveyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class SurveyService implements SurveyInputPort {

    private final SurveyRepository surveyRepository;
    private final PaginationHelper paginationHelper;
    private final SurveyMapper surveyMapper;

    @Override
    public SurveyResponseDto saveSurvey(Survey survey) {
        return surveyMapper.surveyToSurveyResponseDto(surveyRepository.save(survey));
    }

    @Override
    public Optional<SurveyResponseDto> findSurveyById(UUID id) {
        return Optional.ofNullable(surveyMapper.surveyToSurveyResponseDto(this.surveyRepository.findById(id)
                .filter(Survey::getActive).orElse(null)));
    }

    @Override
    public Page<SurveyResponseDto> listSurveys(UUID categoryId, Boolean status, Boolean hasRestrictedAccess, UUID accountId, Boolean isActive, Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = paginationHelper.buildPageRequest(pageNumber, pageSize);

        Specification<Survey> specification = Specification.where(SurveySpecifications.hasCategoryId(categoryId))
                .and(SurveySpecifications.hasActive(isActive))
                .and(SurveySpecifications.hasCategoryId(categoryId))
                .and(SurveySpecifications.hasStatus(status))
                .and(SurveySpecifications.hasRestrictedAccess(hasRestrictedAccess))
                .and(SurveySpecifications.hasCategoryIdAndStatus(categoryId, status))
                .and(SurveySpecifications.hasCategoryIdAndHasRestrictedAccess(categoryId, hasRestrictedAccess))
                .and(SurveySpecifications.hasAccountIdAndIsActive(accountId, isActive));

        Page<Survey> surveyPage = surveyRepository.findAll(specification, pageRequest);

        return surveyPage.map(surveyMapper::surveyToSurveyResponseDto);
    }

    @Override
    public Page<Account> listAccountsBySurveyIdAndSurveyActiveAndUserActive(UUID surveyId, Boolean isActiveSurvey, Boolean isActiveAccount, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = paginationHelper.buildPageRequest(pageNumber, pageSize);

        return this.surveyRepository.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyId, isActiveSurvey, isActiveAccount, pageRequest);
    }

    @Override
    public Optional<SurveyResponseDto> updateSurvey(UUID id, Survey survey) {

        AtomicReference<Optional<SurveyResponseDto>> atomicReference = new AtomicReference<>();

        surveyRepository.findById(id).ifPresentOrElse(surveyFound -> {
            if (Boolean.TRUE.equals(survey.getActive())) {
                Survey updatedSurvey = surveyRepository.save(surveyFound);
                SurveyResponseDto surveyResponseDto = surveyMapper.surveyToSurveyResponseDto(updatedSurvey);

                atomicReference.set(Optional.of(surveyResponseDto));
            } else {
                atomicReference.set(Optional.empty());
            }
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    public Boolean deleteSurvey(UUID id) {

        Optional<Survey> survey = surveyRepository.findById(id);

        if (survey.isPresent() && Boolean.FALSE.equals(!survey.get().getActive())) {
            survey.get().setActive(false);
            surveyRepository.save(survey.get());
            return true;
        }

        return false;
    }
}
