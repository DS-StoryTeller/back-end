package com.cojac.storyteller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {

    @NotBlank(message = "email를 입력해주세요.")
    private String email;
    private String authCode;
    private boolean authResult;
}
