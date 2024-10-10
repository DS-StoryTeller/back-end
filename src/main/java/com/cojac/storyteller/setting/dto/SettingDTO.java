package com.cojac.storyteller.setting.dto;

import com.cojac.storyteller.setting.entity.SettingEntity;
import com.cojac.storyteller.setting.entity.enums.FontSize;
import com.cojac.storyteller.setting.entity.enums.ReadingSpeed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettingDTO {
    private FontSize fontSize;
    private ReadingSpeed readingSpeed;

    public static SettingDTO toDto(SettingEntity settingEntity) {
        return SettingDTO.builder()
                .fontSize(settingEntity.getFontSize())
                .readingSpeed(settingEntity.getReadingSpeed())
                .build();
    }
}
