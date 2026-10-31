package com.dev.myfinance.mapper;

import com.dev.myfinance.dto.request.FinanceRequest;
import com.dev.myfinance.dto.response.FinancePageResponse;
import com.dev.myfinance.dto.response.FinanceResponse;
import com.dev.myfinance.persistence.entities.FinanceEntity;
import com.dev.myfinance.persistence.entities.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FinanceMapper {

    @Mapping(target = "operationId", ignore = true)
    @Mapping(target = "operationDescription", source = "request.description")
    @Mapping(target = "operationCategory", source = "request.category")
    @Mapping(target = "operationTag", source = "tagEntities")
    @Mapping(target = "operationValue", source = "request.value")
    @Mapping(target = "operationDate", source = "request.date")
    FinanceEntity toEntity(FinanceRequest request, List<TagEntity> tagEntities);

    default List<TagEntity> mapStringToTagEntityList(String tag) {
        if (tag == null || tag.isBlank()) {
            return Collections.emptyList();
        }
        TagEntity tagEntity = new TagEntity();
        tagEntity.setTagDescription(tag);
        return List.of(tagEntity);
    }

    @Mapping(target = "operationTags", source = "operationTag")
    FinanceResponse toResponse(FinanceEntity entity);

    default String mapTagToString(TagEntity tagEntity) {
        if (tagEntity == null) {
            return null;
        }
        return tagEntity.getTagDescription(); // Ajuste para o getter correspondente na sua TagEntity
    }

    default FinancePageResponse toPageResponse(Page<FinanceEntity> page) {
        if (page == null) {
            return null;
        }

        List<FinanceResponse> content = page.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new FinancePageResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
