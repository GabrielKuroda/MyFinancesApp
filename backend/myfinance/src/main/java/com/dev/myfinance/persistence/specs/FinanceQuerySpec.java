package com.dev.myfinance.persistence.specs;

import com.dev.myfinance.persistence.entities.FinanceEntity;
import com.dev.myfinance.persistence.entities.TagEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class FinanceQuerySpec {

    private FinanceQuerySpec() {
    }

    private static Specification<FinanceEntity> withOperationCategory(String category) {
        return (root, query, builder) ->
                (category == null || category.isBlank())
                        ? null
                        : builder.equal(root.get("operationCategory"), category); // Usa o atributo Java em camelCase
    }

    private static Specification<FinanceEntity> betweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, builder) -> {
            if (startDate != null && endDate != null) {
                return builder.between(root.get("operationDate"), startDate, endDate);
            }
            if (startDate != null) {
                return builder.greaterThanOrEqualTo(root.get("operationDate"), startDate);
            }
            if (endDate != null) {
                return builder.lessThanOrEqualTo(root.get("operationDate"), endDate);
            }
            return null;
        };
    }

    private static Specification<FinanceEntity> withTagIds(List<Long> tagIds) {
        return (root, query, builder) -> {
            if (tagIds == null || tagIds.isEmpty()) {
                return null;
            }

            query.distinct(true);

            Join<FinanceEntity, TagEntity> tagsJoin = root.join("operationTag");

            return tagsJoin.get("tagId").in(tagIds);
        };
    }

    public static Specification<FinanceEntity> generateQueryFilter(
            String category,
            LocalDate startDate,
            LocalDate endDate,
            List<Long> tagIds
    ) {
        return Specification
                .where(withOperationCategory(category))
                .and(betweenDates(startDate, endDate))
                .and(withTagIds(tagIds));
    }

}
