package com.cojac.storyteller.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocalUserDTO implements UserDTO{

    private Integer id;
    private String username;
    private String password;
    private String email;
    private String role;

    public LocalUserDTO(Integer id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
