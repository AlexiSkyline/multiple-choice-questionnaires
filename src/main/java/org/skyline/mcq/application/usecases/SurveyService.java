package org.skyline.mcq.application.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.AnswersDto;
import org.skyline.mcq.application.dtos.input.SurveyAnswersDto;
import org.skyline.mcq.application.dtos.input.SurveyRequestDto;
import org.skyline.mcq.application.dtos.input.SurveyUpdateRequestDto;
import org.skyline.mcq.application.dtos.output.AccountSummaryDto;
import org.skyline.mcq.application.dtos.output.ResultResponseDto;
import org.skyline.mcq.application.dtos.output.SurveyResponseDto;
import org.skyline.mcq.application.mappings.AccountMapper;
import org.skyline.mcq.application.mappings.ResultMapper;
import org.skyline.mcq.application.mappings.SurveyMapper;
import org.skyline.mcq.application.utils.PaginationHelper;
import org.skyline.mcq.domain.models.*;
import org.skyline.mcq.domain.specification.SurveySpecifications;
import org.skyline.mcq.infrastructure.inputport.SurveyInputPort;
import org.skyline.mcq.infrastructure.outputport.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class SurveyService implements SurveyInputPort {

    private final SurveyRepository surveyRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final PaginationHelper paginationHelper;
    private final SurveyMapper surveyMapper;
    private final AccountMapper accountMapper;
    private final ResultMapper resultMapper;
    private final ObjectMapper objectMapper;

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
    public Optional<SurveyResponseDto> findSurveyByIdAndAccountId(UUID id, UUID accountId) {
        return Optional.ofNullable(surveyMapper.surveyToSurveyResponseDto(this.surveyRepository.findByIdAndAccountId(id, accountId)
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

        if (survey.isPresent() && Boolean.TRUE.equals(survey.get().getActive())) {
            survey.get().setActive(false);
            surveyRepository.save(survey.get());
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public Optional<ResultResponseDto> submitSurvey(SurveyAnswersDto surveyAnswersDto, UUID accountId) {
        Optional<Survey> survey = surveyRepository.findById(surveyAnswersDto.getSurveyId()).filter(Survey::getActive);
        Optional<Account> account = accountRepository.findById(accountId).filter(Account::getActive);

        if (survey.isEmpty() || account.isEmpty()) return Optional.empty();

        Result result = calculationResult(surveyAnswersDto, account.get());
        result.setAccount(account.get());
        result.setSurvey(survey.get());

        return Optional.of(resultMapper.resultToResultResponseDto(resultRepository.save(result)));
    }

    private Result calculationResult(SurveyAnswersDto surveyAnswersDto, Account account) {
        double maximumPoints = 0;
        int correctQuestion = 0;
        int incorrectAnswers = 0;
        Set<Answer> userAnswers = new HashSet<>();
        Optional<Question> optionalQuestion;

        for (AnswersDto answer : surveyAnswersDto.getAnswers()) {
            Map<String, Object> userAnswer = objectMapper.convertValue(answer.getUserAnswers(), Map.class);

            optionalQuestion = questionRepository.findById(answer.getQuestionId());
            if (optionalQuestion.isPresent()) {
                Question question = optionalQuestion.get();
                Map<String, Object> correctAnswers = objectMapper.convertValue(question.getCorrectAnswers(), Map.class);
                int correctCount = 0;

                for (Map.Entry<String, Object> entry : userAnswer.entrySet()) {
                    String userOption = entry.getKey();
                    Object userSelectedAnswer = entry.getValue();

                    if (correctAnswers.containsKey(userOption) && correctAnswers.get(userOption).equals(userSelectedAnswer)) correctCount++;
                }

                if (correctCount == question.getAllowedAnswers()) correctQuestion++;
                if (correctCount == 0) incorrectAnswers++;

                double currentPoint = ((double) correctCount / correctAnswers.size()) * question.getPoints();
                maximumPoints += currentPoint;

                userAnswers.add(
                    Answer.builder()
                        .account(account)
                        .question(question)
                        .userAnswers(answer.getUserAnswers())
                        .isCorrect(correctCount == question.getAllowedAnswers())
                        .points((int) currentPoint)
                        .build()
                );
            }
        }

        Timestamp startTime = Timestamp.valueOf(surveyAnswersDto.getStartTime());
        Timestamp endTime = Timestamp.valueOf(surveyAnswersDto.getEndTime());
        Integer durationMillis = Math.toIntExact(endTime.getTime() - startTime.getTime());

        return Result.builder()
                .startTime(startTime)
                .endTime(endTime)
                .duration(durationMillis)
                .totalPoints((int) maximumPoints)
                .correctAnswers(correctQuestion)
                .incorrectAnswers(incorrectAnswers)
                .answers(userAnswers)
                .build();
    }
}
