package com.dev.myfinance.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(name = "TAG_DESCRIPTION")
    private String tagDescription;

    @ManyToMany(mappedBy = "operationTag")
    private List<FinanceEntity> financeEntityList = new ArrayList<>();

}
