package com.cojac.storyteller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsernameDTO {

    @NotBlank(message = "Username을 입력해주세요.")
    private String username;
    private boolean authResult;
}
