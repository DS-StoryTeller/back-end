package com.cojac.storyteller.domain;

import com.cojac.storyteller.domain.setting.FontSize;
import com.cojac.storyteller.domain.setting.ReadingSpeed;
import com.cojac.storyteller.dto.setting.SettingDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private FontSize fontSize;

    @Enumerated(EnumType.STRING)
    private ReadingSpeed readingSpeed;

    public void updateSetting(SettingDTO settingDTO) {
        this.fontSize = settingDTO.getFontSize() == null ? this.fontSize : settingDTO.getFontSize();
        this.readingSpeed = settingDTO.getReadingSpeed() == null ? this.readingSpeed : settingDTO.getReadingSpeed();
    }

    // 초기화 메서드
    public static SettingEntity createDefaultSetting() {
        return SettingEntity.builder()
                .fontSize(FontSize.MEDIUM)
                .readingSpeed(ReadingSpeed.NORMAL)
                .build();
    }
}
