package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.LocalUserEntityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<LocalUserEntityEntity, Integer> {

    Boolean existsByUsername(String username);

    Optional<LocalUserEntityEntity> findByUsername(String username);
}
