package com.cojac.storyteller.user.dto.oauth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleLoginRequestDTO {

    @NotBlank(message = "idToken을 입력해주세요.")
    private String idToken;
    @NotBlank(message = "role를 입력해주세요.")
    private String role;

}
