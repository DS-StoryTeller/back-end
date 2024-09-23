package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

    List<ProfileEntity> findByUser(UserEntity user);
}
