package com.dev.myfinance.dto.request;

import com.dev.myfinance.dto.enums.CategoryEnum;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FinanceRequest(
        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String description,

        @NotNull(message = "A categoria é obrigatória")
        CategoryEnum category,

        List<@NotBlank(message = "O nome da tag não pode ser em branco") String> tags,

        @NotNull(message = "O valor é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal value,

        @NotNull(message = "A data é obrigatória")
        @PastOrPresent(message = "A data não pode ser futura")
        LocalDate date
) {}