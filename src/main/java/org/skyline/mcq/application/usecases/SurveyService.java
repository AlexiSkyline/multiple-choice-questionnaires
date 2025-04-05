package org.skyline.mcq.application.usecases;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.SurveyRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.application.mappings.AccountMapper;
import org.skyline.mcq.application.mappings.SurveyMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.domain.models.Survey;
import org.skyline.mcq.domain.specification.SurveySpecifications;
import org.skyline.mcq.infrastructure.inputport.SurveyInputPort;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.skyline.mcq.infrastructure.outputport.SurveyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class SurveyService implements SurveyInputPort {

    private final SurveyRepository surveyRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final PaginationHelper paginationHelper;
    private final SurveyMapper surveyMapper;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public Optional<SurveyResponseDto> saveSurvey(UUID accountId, SurveyRequestDto survey) {

        Optional<Account> account = accountRepository.findById(accountId).filter(Account::getActive);
        Optional<Category> category = categoryRepository.findById(survey.getCategoryId()).filter(Category::getActive);

        if (account.isEmpty() || category.isEmpty()) return Optional.empty();

        Survey newSurvey = surveyMapper.surveyRequesttDtoToSurvey(survey);
        newSurvey.setAccount(account.get());
        newSurvey.setCategory(category.get());

        return Optional.of(surveyMapper.surveyToSurveyResponseDto(surveyRepository.save(newSurvey)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SurveyResponseDto> findSurveyById(UUID id) {
        return Optional.ofNullable(surveyMapper.surveyToSurveyResponseDto(this.surveyRepository.findById(id)
                .filter(Survey::getActive).orElse(null)));
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<AccountSummaryDto> listAccountsBySurveyIdAndSurveyActiveAndUserActive(UUID surveyId, Boolean isActiveSurvey, Boolean isActiveAccount, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = paginationHelper.buildPageRequest(pageNumber, pageSize);

        return this.surveyRepository.listAccountsBySurveyIdAndSurveyActiveAndUserActive(surveyId, isActiveSurvey, isActiveAccount, pageRequest)
                .map(accountMapper::accountToAccountResponseDto);
    }

    @Override
    @Transactional
    public Optional<SurveyResponseDto> updateSurvey(UUID id, UUID accountId, SurveyUpdateRequestDto survey) {

        AtomicReference<Optional<SurveyResponseDto>> atomicReference = new AtomicReference<>();

        surveyRepository.findByIdAndAccountId(id, accountId).ifPresentOrElse(surveyFound -> {
            if (Boolean.TRUE.equals(surveyFound.getActive())) {
                surveyMapper.updateSurveyFromSurveyUpdateRequestDto(survey, surveyFound);
                SurveyResponseDto surveyResponseDto = surveyMapper.surveyToSurveyResponseDto(surveyRepository.save(surveyFound));

                atomicReference.set(Optional.of(surveyResponseDto));
            } else {
                atomicReference.set(Optional.empty());
            }
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    @Transactional
    public Boolean deleteSurvey(UUID id, UUID accountId) {

        Optional<Survey> survey = surveyRepository.findByIdAndAccountId(id, accountId);

        if (survey.isPresent() && Boolean.FALSE.equals(!survey.get().getActive())) {
            survey.get().setActive(false);
            surveyRepository.save(survey.get());
            return true;
        }

        return false;
    }
}
