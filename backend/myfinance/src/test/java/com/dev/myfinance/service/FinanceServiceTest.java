package com.dev.myfinance.service;

import com.dev.myfinance.dto.enums.CategoryEnum;
import com.dev.myfinance.dto.request.FinanceRequest;
import com.dev.myfinance.dto.response.FinancePageResponse;
import com.dev.myfinance.dto.response.FinanceResponse;
import com.dev.myfinance.exceptions.BusinessValidationException;
import com.dev.myfinance.exceptions.CustomNotFoundException;
import com.dev.myfinance.mapper.FinanceMapper;
import com.dev.myfinance.persistence.entities.FinanceEntity;
import com.dev.myfinance.persistence.entities.TagEntity;
import com.dev.myfinance.persistence.repository.FinanceRepository;
import com.dev.myfinance.persistence.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private FinanceRepository repository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private FinanceMapper mapper;

    @InjectMocks
    private FinanceService financeService;

    @Nested
    @DisplayName("Testes do método create")
    class CreateTests {

        @Test
        @DisplayName("Deve criar um registro com sucesso quando todas as tags existirem")
        void shouldCreateFinanceWhenAllTagsExist() {
            // Arrange
            List<String> tags = List.of("Salario", "Investimento");
            FinanceRequest request = new FinanceRequest(
                    "Entrada de salário",
                    CategoryEnum.INCOME,
                    tags,
                    new BigDecimal("5000.00"),
                    LocalDate.of(2026, 10, 18)
            );

            TagEntity tag1 = new TagEntity();
            tag1.setTagDescription("Salario");
            TagEntity tag2 = new TagEntity();
            tag2.setTagDescription("Investimento");
            List<TagEntity> existingTags = List.of(tag1, tag2);

            FinanceEntity unpersistedEntity = new FinanceEntity();
            FinanceEntity savedEntity = new FinanceEntity();
            savedEntity.setOperationId(10L);

            when(tagRepository.findAllByTagDescriptionIn(anyList())).thenReturn(existingTags);
            when(mapper.toEntity(eq(request), eq(existingTags))).thenReturn(unpersistedEntity);
            when(repository.save(unpersistedEntity)).thenReturn(savedEntity);

            // Act
            Long generatedId = financeService.create(request);

            // Assert
            assertThat(generatedId).isEqualTo(10L);
            verify(tagRepository, times(1)).findAllByTagDescriptionIn(anyList());
            verify(mapper, times(1)).toEntity(request, existingTags);
            verify(repository, times(1)).save(unpersistedEntity);
        }

        @Test
        @DisplayName("Deve criar um registro com sucesso quando a lista de tags for nula")
        void shouldCreateFinanceWhenTagsIsNull() {
            // Arrange
            FinanceRequest request = new FinanceRequest(
                    "Aluguel",
                    CategoryEnum.OUTCOME,
                    null,
                    new BigDecimal("1200.00"),
                    LocalDate.of(2026, 10, 18)
            );

            FinanceEntity unpersistedEntity = new FinanceEntity();
            FinanceEntity savedEntity = new FinanceEntity();
            savedEntity.setOperationId(20L);

            when(mapper.toEntity(eq(request), eq(Collections.emptyList()))).thenReturn(unpersistedEntity);
            when(repository.save(unpersistedEntity)).thenReturn(savedEntity);

            // Act
            Long generatedId = financeService.create(request);

            // Assert
            assertThat(generatedId).isEqualTo(20L);
            verify(tagRepository, never()).findAllByTagDescriptionIn(anyList());
            verify(mapper, times(1)).toEntity(request, Collections.emptyList());
            verify(repository, times(1)).save(unpersistedEntity);
        }

        @Test
        @DisplayName("Deve criar um registro com sucesso quando a lista de tags for vazia")
        void shouldCreateFinanceWhenTagsIsEmpty() {
            // Arrange
            FinanceRequest request = new FinanceRequest(
                    "Supermercado",
                    CategoryEnum.OUTCOME,
                    Collections.emptyList(),
                    new BigDecimal("250.00"),
                    LocalDate.of(2026, 10, 18)
            );

            FinanceEntity unpersistedEntity = new FinanceEntity();
            FinanceEntity savedEntity = new FinanceEntity();
            savedEntity.setOperationId(30L);

            when(mapper.toEntity(eq(request), eq(Collections.emptyList()))).thenReturn(unpersistedEntity);
            when(repository.save(unpersistedEntity)).thenReturn(savedEntity);

            // Act
            Long generatedId = financeService.create(request);

            // Assert
            assertThat(generatedId).isEqualTo(30L);
            verify(tagRepository, never()).findAllByTagDescriptionIn(anyList());
            verify(repository, times(1)).save(unpersistedEntity);
        }

        @Test
        @DisplayName("Deve lançar BusinessValidationException quando uma ou mais tags não existirem no banco")
        void shouldThrowBusinessValidationExceptionWhenTagsDoNotExist() {
            // Arrange
            List<String> requestedTags = List.of("Existe", "NaoExiste1", "NaoExiste2");
            FinanceRequest request = new FinanceRequest(
                    "Compra diversa",
                    CategoryEnum.OUTCOME,
                    requestedTags,
                    new BigDecimal("80.00"),
                    LocalDate.of(2026, 10, 18)
            );

            TagEntity existingTag = new TagEntity();
            existingTag.setTagDescription("Existe");

            when(tagRepository.findAllByTagDescriptionIn(anyList())).thenReturn(List.of(existingTag));

            // Act & Assert
            assertThatThrownBy(() -> financeService.create(request))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("As seguintes tags não existem: [NaoExiste1, NaoExiste2]");

            verify(tagRepository, times(1)).findAllByTagDescriptionIn(anyList());
            verify(mapper, never()).toEntity(any(), any());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método getAllFinances")
    class GetAllFinancesTests {

        @Test
        @DisplayName("Deve retornar página de finanças quando houver conteúdo no repositório")
        void shouldReturnFinancePageResponseWhenDataExists() {
            // Arrange
            String category = "INCOME";
            LocalDate startDate = LocalDate.of(2026, 10, 1);
            LocalDate endDate = LocalDate.of(2026, 10, 31);
            List<Long> tagIds = List.of(1L);
            int page = 0;
            int size = 10;

            FinanceEntity entity = new FinanceEntity();
            Page<FinanceEntity> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(page, size), 1);

            FinanceResponse responseItem = new FinanceResponse(
                    "Salário",
                    "INCOME",
                    List.of("Trabalho"),
                    BigDecimal.valueOf(5000.0),
                    LocalDate.of(2026, 10, 18)
            );

            FinancePageResponse expectedPageResponse = new FinancePageResponse(
                    List.of(responseItem),
                    0,
                    10,
                    1L,
                    1,
                    true
            );

            when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(entityPage);
            when(mapper.toPageResponse(entityPage)).thenReturn(expectedPageResponse);

            // Act
            FinancePageResponse result = financeService.getAllFinances(category, startDate, endDate, tagIds, page, size);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.financeContent()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1L);

            verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            verify(mapper, times(1)).toPageResponse(entityPage);
        }

        @Test
        @DisplayName("Deve lançar CustomNotFoundException quando o repositório retornar página sem registros")
        void shouldThrowCustomNotFoundExceptionWhenPageIsEmpty() {
            // Arrange
            Page<FinanceEntity> emptyPage = Page.empty(PageRequest.of(0, 10));

            when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

            // Act & Assert
            assertThatThrownBy(() -> financeService.getAllFinances(null, null, null, null, 0, 10))
                    .isInstanceOf(CustomNotFoundException.class);

            verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            verify(mapper, never()).toPageResponse(any());
        }
    }
}