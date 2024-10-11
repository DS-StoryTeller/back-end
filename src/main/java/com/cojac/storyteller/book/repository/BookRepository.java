package com.cojac.storyteller.book.repository;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    Page<BookEntity> findByProfile(ProfileEntity profile, Pageable pageable);

    List<BookEntity> findByProfile(ProfileEntity profile);

    Optional<BookEntity> findByIdAndProfile(Integer id, ProfileEntity profile);

    @Query("SELECT b FROM BookEntity b JOIN FETCH b.setting WHERE b.id = :bookId AND b.profile = :profile")
    Optional<BookEntity> findByIdAndProfileWithSetting(Integer bookId, ProfileEntity profile);

    // 즐겨찾기 책 필터링
    Page<BookEntity> findByProfileAndIsFavoriteTrue(ProfileEntity profile, Pageable pageable);

    // 읽고 있는 책 필터링
    Page<BookEntity> findByProfileAndIsReadingTrue(ProfileEntity profile, Pageable pageable);
}
