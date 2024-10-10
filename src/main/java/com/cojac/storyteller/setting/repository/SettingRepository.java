package com.cojac.storyteller.setting.repository;

import com.cojac.storyteller.setting.entity.SettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<SettingEntity, Integer> {
}
