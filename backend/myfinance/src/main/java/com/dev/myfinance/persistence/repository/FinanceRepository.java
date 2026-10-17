package com.dev.myfinance.persistence.repository;

import com.dev.myfinance.persistence.entities.FinanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceRepository extends JpaRepository<FinanceEntity, Long> {
}
