package org.skyline.mcq.domain.specification;

import jakarta.persistence.criteria.Predicate;
import org.skyline.mcq.domain.models.Survey;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SurveySpecifications {

    public static Specification<Survey> hasActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                isActive != null ? criteriaBuilder.equal(root.get("active"), isActive) : criteriaBuilder.conjunction();
    }

    public static Specification<Survey> hasCategoryId(UUID categoryId) {
        return (root, query, criteriaBuilder) ->
                categoryId != null ? criteriaBuilder.equal(root.get("category").get("id"), categoryId) : criteriaBuilder.conjunction();
    }

    public static Specification<Survey> hasStatus(Boolean status) {
        return (root, query, critealBuilder) ->
                status != null ? critealBuilder.equal(root.get("status"), status) : critealBuilder.conjunction();
    }

    public static Specification<Survey> hasRestrictedAccess(Boolean isPublic) {
        return (root, query, criteriaBuilder) ->
            isPublic != null ? criteriaBuilder.equal(root.get("hasRestrictedAccess"), isPublic) : criteriaBuilder.conjunction();
    }

    public static Specification<Survey> hasCategoryIdAndStatus(UUID categoryId, Boolean status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Survey> hasCategoryIdAndHasRestrictedAccess(UUID categoryId, Boolean isPublic) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (isPublic != null) {
                predicates.add(criteriaBuilder.equal(root.get("hasRestrictedAccess"), isPublic));
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Survey> hasAccountIdAndIsActive(UUID accountId, Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (accountId != null) {
                predicates.add(criteriaBuilder.equal(root.get("account").get("id"), accountId));
            }

            if (isActive != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), isActive));
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
