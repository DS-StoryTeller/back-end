package com.cojac.storyteller.user.dto.oauth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoLoginRequestDTO {

    @NotNull(message = "id를 입력해주세요.")
    private String id;
    @NotBlank(message = "role를 입력해주세요.")
    private String role;
    @NotBlank(message = "nickname을 입력해주세요.")
    private String nickname;
    @NotBlank(message = "email를 입력해주세요.")
    private String email;

}
