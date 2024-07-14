package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.SocialUserEntityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialUserRepository extends JpaRepository<SocialUserEntityEntity, Integer> {

    SocialUserEntityEntity findByAccountId(String accountId);
}
