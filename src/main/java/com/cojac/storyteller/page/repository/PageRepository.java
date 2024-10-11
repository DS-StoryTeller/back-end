package com.cojac.storyteller.page.repository;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.page.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, Integer> {
    Optional<PageEntity> findByBookAndPageNumber(BookEntity book, Integer pageNumber);

    @Query("SELECT p FROM PageEntity p LEFT JOIN FETCH p.unknownWords WHERE p.book = :book AND p.pageNumber = :pageNumber")
    Optional<PageEntity> findPageWithUnknownWords(@Param("book") BookEntity book, @Param("pageNumber") Integer pageNumber);
}
