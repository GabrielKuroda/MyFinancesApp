package com.dev.myfinance.persistence.repository;

import com.dev.myfinance.persistence.entities.FinanceEntity;
import com.dev.myfinance.persistence.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    List<TagEntity> findAllByTagDescriptionIn(List<String> tagDescriptions);

    boolean existsByTagDescription(String tagDescription);
}
