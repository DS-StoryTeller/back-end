package com.cojac.storyteller.user.repository;

import com.cojac.storyteller.user.entity.LocalUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalUserRepository extends JpaRepository<LocalUserEntity, Integer> {

    Boolean existsByUsername(String username);
    Optional<LocalUserEntity> findByUsername(String username);
    Boolean existsByEmail(String email);
}
