package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.SettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<SettingEntity, Integer> {
}
