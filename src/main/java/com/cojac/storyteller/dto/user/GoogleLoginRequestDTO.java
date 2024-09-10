package com.cojac.storyteller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleLoginRequestDTO {

    @NotBlank(message = "idToken을 입력해주세요.")
    private String idToken;
    @NotBlank(message = "role를 입력해주세요.")
    private String role;

}
