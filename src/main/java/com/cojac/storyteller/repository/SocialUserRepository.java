package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.SocialUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialUserRepository extends JpaRepository<SocialUserEntity, Integer> {
    Boolean existsByAccountId(String accountId);
    Optional<SocialUserEntity> findByAccountId(String accountId);
    Boolean existsByEmail(String email);
}
