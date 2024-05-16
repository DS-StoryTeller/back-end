package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.PageEntity;
import com.cojac.storyteller.domain.UnknownWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnknownWordRepository extends JpaRepository<UnknownWordEntity, Integer> {
    Optional<List<UnknownWordEntity>> getByPage(PageEntity page);
}
