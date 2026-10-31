package com.dev.myfinance.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FinanceResponse (
        String operationDescription,
        String operationCategory,
        List<String> operationTags,
        BigDecimal operationValue,
        LocalDate operationDate
){}
