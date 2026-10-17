package com.dev.myfinance.persistence.repository;

import com.dev.myfinance.persistence.entities.FinanceEntity;
import com.dev.myfinance.persistence.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
}
