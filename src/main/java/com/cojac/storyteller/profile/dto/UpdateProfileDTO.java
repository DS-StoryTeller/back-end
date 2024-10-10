package com.cojac.storyteller.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {

    @NotBlank(message = "name을 입력해주세요.")
    private String name;
    @NotNull(message = "birthDate를 입력해주세요.")
    private LocalDate birthDate;
    @NotBlank(message = "imageUrl를 입력해주세요.")
    private String imageUrl;
    @NotBlank(message = "pinNumber를 입력해주세요.")
    private String pinNumber;
}
