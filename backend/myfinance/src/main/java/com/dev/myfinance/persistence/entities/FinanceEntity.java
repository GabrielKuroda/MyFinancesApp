package com.dev.myfinance.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FinanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationId;

    @Column(name = "OPERATION_DESCRIPTION")
    private String operationDescription;

    @Column(name = "OPERATION_CATEGORY")
    private String operationCategory;

    @ManyToMany
    @JoinTable(
            name = "OPERATION_TAG_JOIN",
            joinColumns = @JoinColumn(name = "OPERATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID")
    )
    private List<TagEntity> operationTag;

    @Column(name = "OPERATION_VALUE")
    private Double operationValue;

    @Column(name = "OPERATION_DATE")
    private LocalDate operationDate;
}
