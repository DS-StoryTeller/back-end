package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    private String role;

    public UserEntity(String encryptedPassword, String username, String role) {
        this.password = encryptedPassword;
        this.username = username;
        this.role = role;
    }

}
