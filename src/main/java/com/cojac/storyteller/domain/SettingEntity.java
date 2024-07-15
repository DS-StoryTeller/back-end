package com.cojac.storyteller.domain;

import com.cojac.storyteller.domain.setting.FontSize;
import com.cojac.storyteller.domain.setting.ReadingSpeed;
import com.cojac.storyteller.dto.setting.SettingDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.cojac.storyteller.domain.setting.FontSize.MEDIUM;
import static com.cojac.storyteller.domain.setting.ReadingSpeed.NORMAL;

@Entity
@Getter
@NoArgsConstructor
public class SettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private FontSize fontSize;

    @Enumerated(EnumType.STRING)
    private ReadingSpeed readingSpeed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    public SettingEntity(BookEntity book) {
        this.book = book;
        this.fontSize = MEDIUM;
        this.readingSpeed = NORMAL;
    }

    public void updateSetting(SettingDTO settingDTO) {
        this.fontSize = settingDTO.getFontSize() == null ? this.fontSize : settingDTO.getFontSize();
        this.readingSpeed = settingDTO.getReadingSpeed() == null ? this.readingSpeed : settingDTO.getReadingSpeed();
    }
}
