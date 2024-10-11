package com.cojac.storyteller.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {

    @Email(message = "유효한 이메일 주소를 입력하세요")
    private String email;
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "값은 알파벳과 숫자만 포함해야 합니다")
    private String authCode;
    private boolean authResult;
}
