package com.cojac.storyteller.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequestDTO {

    @NotBlank(message = "username을 입력하세요")
    private String username;

    @NotBlank(message = "password를 입력하세요")
    private String password;

    @Email(message = "email를 입력하세요")
    @NotBlank(message = "email를 입력하세요")
    private String email;

    @NotBlank(message = "role를 입력하세요")
    private String role;
}
