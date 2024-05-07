package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, Integer> {
    Optional<PageEntity> findByBookAndPageNumber(BookEntity book, Integer pageNumber);
}
