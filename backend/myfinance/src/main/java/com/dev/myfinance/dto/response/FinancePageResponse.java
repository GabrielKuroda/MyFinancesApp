package com.dev.myfinance.dto.response;

import java.util.List;

public record FinancePageResponse(
        List<FinanceResponse> financeContent,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast
) {}
