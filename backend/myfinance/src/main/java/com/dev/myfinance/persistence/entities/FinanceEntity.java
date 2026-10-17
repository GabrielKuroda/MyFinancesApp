package com.dev.myfinance.persistence.entities;

import com.dev.myfinance.dto.enums.CategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FinanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationId;

    @Column(name = "operationDescription")
    private String operationDescription;

    @Column(name = "operationCategory")
    private String operationCategory;

    //ToDo Depois ao criar fluxo de Tags
//    @Column(name = "tag")
//    private String tag;

    @Column(name = "operationValue")
    private Double operationValue;

    @Column(name = "operationDate")
    private LocalDate operationDate;
}
