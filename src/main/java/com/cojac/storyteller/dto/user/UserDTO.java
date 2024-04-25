package com.cojac.storyteller.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Integer id;
    private String username;
    private String password;
    private String role;
    private String refreshToken;

    public UserDTO(Integer id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

}
