package com.cojac.storyteller.dto.setting;

import com.cojac.storyteller.domain.FontSize;
import com.cojac.storyteller.domain.ReadingSpeed;
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
}
