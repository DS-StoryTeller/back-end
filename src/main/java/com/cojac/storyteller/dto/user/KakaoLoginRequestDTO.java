package com.cojac.storyteller.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequestDTO {

    @NotNull(message = "id를 입력해주세요.")
    private String id;
    @NotBlank(message = "role를 입력해주세요.")
    private String role;
    @NotBlank(message = "username를 입력해주세요.")
    private String username;
    @NotBlank(message = "email를 입력해주세요.")
    private String email;

}
