package com.cojac.storyteller.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class LocalUserDTO implements UserDTO{

    private Integer id;
    @NotBlank(message = "username을 입력하세요")
    private String username;
    @NotBlank(message = "password를 입력하세요")
    private String password;
    @Email(message = "email를 입력하세요")
    private String email;
    @NotBlank(message = "role를 입력하세요")
    private String role;

    @Builder
    public LocalUserDTO(Integer id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
