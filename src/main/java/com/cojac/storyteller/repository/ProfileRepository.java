package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

    List<ProfileEntity> findByUser_Id(Integer userId);
}
