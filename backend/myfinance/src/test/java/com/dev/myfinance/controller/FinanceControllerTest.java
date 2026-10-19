package com.dev.myfinance.controller;

import com.dev.myfinance.dto.enums.CategoryEnum;
import com.dev.myfinance.dto.request.FinanceRequest;
import com.dev.myfinance.dto.response.FinancePageResponse;
import com.dev.myfinance.dto.response.FinanceResponse;
import com.dev.myfinance.service.FinanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinanceController.class)
class FinanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FinanceService financeService;

    @Nested
    @DisplayName("POST /finance/insert")
    class CreateFinanceTests {

        @Test
        @DisplayName("Deve retornar status 201 Created e Header Location ao inserir com sucesso")
        void shouldCreateFinanceSuccessfully() throws Exception {
            FinanceRequest validRequest = new FinanceRequest(
                    "Salário Mensal",
                    CategoryEnum.INCOME,
                    List.of("Trabalho"),
                    new BigDecimal("5000.00"),
                    LocalDate.of(2026, 10, 18)
            );

            when(financeService.create(any(FinanceRequest.class))).thenReturn(1L);

            mockMvc.perform(post("/finance/insert")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/finance/1"))
                    .andExpect(header().exists("Location"));

            verify(financeService, times(1)).create(any(FinanceRequest.class));
        }

        @Test
        @DisplayName("Deve retornar status 201 Created quando a lista de tags for nula ou vazia")
        void shouldCreateFinanceWithoutTags() throws Exception {
            FinanceRequest requestWithoutTags = new FinanceRequest(
                    "Compra Mercado",
                    CategoryEnum.OUTCOME,
                    Collections.emptyList(),
                    new BigDecimal("150.50"),
                    LocalDate.of(2026, 10, 18)
            );

            when(financeService.create(any(FinanceRequest.class))).thenReturn(2L);

            mockMvc.perform(post("/finance/insert")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestWithoutTags)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/finance/2"));

            verify(financeService, times(1)).create(any(FinanceRequest.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 Bad Request quando a descrição for vazia")
        void shouldReturnBadRequestWhenDescriptionIsBlank() throws Exception {
            FinanceRequest invalidRequest = new FinanceRequest(
                    "",
                    CategoryEnum.INCOME,
                    List.of("Trabalho"),
                    new BigDecimal("100.00"),
                    LocalDate.of(2026, 10, 18)
            );

            mockMvc.perform(post("/finance/insert")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(financeService, never()).create(any(FinanceRequest.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 Bad Request quando o valor for zero ou negativo")
        void shouldReturnBadRequestWhenValueIsZeroOrNegative() throws Exception {
            FinanceRequest invalidRequest = new FinanceRequest(
                    "Investimento",
                    CategoryEnum.INCOME,
                    List.of("RendaFixa"),
                    BigDecimal.ZERO, // Inválido: @DecimalMin("0.01")
                    LocalDate.of(2026, 10, 18)
            );

            mockMvc.perform(post("/finance/insert")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(financeService, never()).create(any(FinanceRequest.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 Bad Request quando a data for futura")
        void shouldReturnBadRequestWhenDateIsFuture() throws Exception {
            FinanceRequest invalidRequest = new FinanceRequest(
                    "Recebimento Futuro",
                    CategoryEnum.INCOME,
                    List.of("Freela"),
                    new BigDecimal("200.00"),
                    LocalDate.now().plusDays(10) // Inválido: @PastOrPresent
            );

            mockMvc.perform(post("/finance/insert")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(financeService, never()).create(any(FinanceRequest.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 Bad Request quando a tag na lista for em branco")
        void shouldReturnBadRequestWhenTagItemIsBlank() throws Exception {
            FinanceRequest invalidRequest = new FinanceRequest(
                    "Aluguel",
                    CategoryEnum.OUTCOME,
                    List.of("   "), // Inválido: @NotBlank dentro da lista
                    new BigDecimal("1200.00"),
                    LocalDate.of(2026, 10, 18)
            );

            mockMvc.perform(post("/finance/insert")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(financeService, never()).create(any(FinanceRequest.class));
        }
    }
    @Nested
    @DisplayName("GET /finance/getAll")
    class GetAllFinancesTests {

        @Test
        @DisplayName("Deve retornar 200 OK com parâmetros padrão de paginação")
        void shouldReturnFinancesWithDefaultPagination() throws Exception {
            FinanceResponse item = new FinanceResponse(
                    "Salário Mensal",
                    "INCOME",
                    List.of("Trabalho"),
                    BigDecimal.valueOf(5000.0),
                    LocalDate.of(2026, 10, 18)
            );

            FinancePageResponse pageResponse = new FinancePageResponse(
                    List.of(item),
                    0,
                    10,
                    1L,
                    1,
                    true
            );

            when(financeService.getAllFinances(isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)))
                    .thenReturn(pageResponse);

            mockMvc.perform(get("/finance/getAll")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageSize").value(10))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.isLast").value(true))
                    .andExpect(jsonPath("$.financeContent[0].operationDescription").value("Salário Mensal"))
                    .andExpect(jsonPath("$.financeContent[0].operationTags[0]").value("Trabalho"));

            verify(financeService, times(1))
                    .getAllFinances(isNull(), isNull(), isNull(), isNull(), eq(0), eq(10));
        }

        @Test
        @DisplayName("Deve repassar filtros de busca corretamente para o service")
        void shouldPassAllFiltersToService() throws Exception {
            LocalDate startDate = LocalDate.of(2026, 10, 1);
            LocalDate endDate = LocalDate.of(2026, 10, 31);
            List<Long> tagIds = List.of(1L, 2L);

            FinancePageResponse emptyPageResponse = new FinancePageResponse(
                    Collections.emptyList(),
                    1,
                    5,
                    0L,
                    0,
                    true
            );

            when(financeService.getAllFinances(
                    eq("INCOME"),
                    eq(startDate),
                    eq(endDate),
                    eq(tagIds),
                    eq(1),
                    eq(5)
            )).thenReturn(emptyPageResponse);

            mockMvc.perform(get("/finance/getAll")
                            .param("category", "INCOME")
                            .param("startDate", "2026-10-01")
                            .param("endDate", "2026-10-31")
                            .param("tagIds", "1", "2")
                            .param("page", "1")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.financeContent").isArray())
                    .andExpect(jsonPath("$.financeContent").isEmpty())
                    .andExpect(jsonPath("$.pageNumber").value(1))
                    .andExpect(jsonPath("$.pageSize").value(5));

            verify(financeService, times(1)).getAllFinances(
                    eq("INCOME"),
                    eq(startDate),
                    eq(endDate),
                    eq(tagIds),
                    eq(1),
                    eq(5)
            );
        }
    }
}