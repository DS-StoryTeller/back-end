package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    List<BookEntity> findByProfile(ProfileEntity profile);

    Optional<BookEntity> findByIdAndProfile(Integer id, ProfileEntity profile);
}
