package com.dev.myfinance.service;

import com.dev.myfinance.dto.request.FinanceRequest;
import com.dev.myfinance.dto.response.FinancePageResponse;
import com.dev.myfinance.exceptions.BusinessValidationException;
import com.dev.myfinance.exceptions.CustomNotFoundException;
import com.dev.myfinance.mapper.FinanceMapper;
import com.dev.myfinance.persistence.entities.FinanceEntity;
import com.dev.myfinance.persistence.entities.TagEntity;
import com.dev.myfinance.persistence.repository.FinanceRepository;
import com.dev.myfinance.persistence.repository.TagRepository;
import com.dev.myfinance.persistence.specs.FinanceQuerySpec;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FinanceService {

    private final FinanceRepository repository;
    private final TagRepository tagRepository;
    private final FinanceMapper mapper;

    public FinanceService(
            FinanceRepository repository,
            TagRepository tagRepository,
            FinanceMapper mapper) {
        this.repository = repository;
        this.tagRepository = tagRepository;
        this.mapper = mapper;
    }

    public FinancePageResponse getAllFinances(
            String category,
            LocalDate startDate,
            LocalDate endDate,
            List<Long> tagIds,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<FinanceEntity> financeEntitySpecification =
                FinanceQuerySpec.generateQueryFilter(
                        category,
                        startDate,
                        endDate,
                        tagIds);

        Page<FinanceEntity> allFinancesPage = repository.findAll(financeEntitySpecification, pageable);

        if(!allFinancesPage.hasContent()) {
            throw new CustomNotFoundException("Finance");
        }

        return mapper.toPageResponse(allFinancesPage);
    }

    @Transactional
    public Long create(FinanceRequest request) {
        List<TagEntity> tagEntities = validTags(request.tags());
        FinanceEntity entity = mapper.toEntity(request, tagEntities);
        FinanceEntity saved = repository.save(entity);
        return saved.getOperationId();
    }

    private List<TagEntity> validTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<String> requestedTags = tags.stream()
                    .filter(tag -> tag != null && !tag.isBlank())
                    .distinct()
                    .toList();

            List<TagEntity> existingTags = tagRepository.findAllByTagDescriptionIn(requestedTags);

            Set<String> existingTagNames = existingTags.stream()
                    .map(TagEntity::getTagDescription)
                    .collect(Collectors.toSet());

            List<String> missingTags = requestedTags.stream()
                    .filter(tag -> !existingTagNames.contains(tag))
                    .toList();

            if (!missingTags.isEmpty()) {
                throw new BusinessValidationException("tags", "As seguintes tags não existem: " + missingTags);
            }

            return existingTags;
        }
    }
}
