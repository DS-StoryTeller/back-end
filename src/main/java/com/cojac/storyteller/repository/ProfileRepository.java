package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

}
