package com.cojac.storyteller.profile.repository;

import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

    List<ProfileEntity> findByUser(UserEntity user);
}
