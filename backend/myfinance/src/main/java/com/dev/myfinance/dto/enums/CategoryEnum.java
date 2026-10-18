package com.dev.myfinance.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CategoryEnum {
    INCOME("Income"),
    OUTCOME("Outcome");

    String value;
}
