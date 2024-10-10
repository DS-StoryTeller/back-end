package com.cojac.storyteller.unknownWord.repository;

import com.cojac.storyteller.page.entity.PageEntity;
import com.cojac.storyteller.unknownWord.entity.UnknownWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnknownWordRepository extends JpaRepository<UnknownWordEntity, Integer> {
    Optional<List<UnknownWordEntity>> getByPage(PageEntity page);
}
