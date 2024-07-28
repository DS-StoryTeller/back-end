package com.cojac.storyteller.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PinNumberDTO {

    @NotBlank(message = "pinNumber를 입력해주세요.")
    @Pattern(regexp = "^[0-9]{4}$", message = "값은 4자리 숫자만 포함해야 합니다")
    private String pinNumber;
}
