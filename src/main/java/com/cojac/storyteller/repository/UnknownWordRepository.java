package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.UnknownWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnknownWordRepository extends JpaRepository<UnknownWordEntity, Integer> {
}
