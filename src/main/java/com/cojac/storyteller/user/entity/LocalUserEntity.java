package com.cojac.storyteller.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue("L")
public class LocalUserEntity extends UserEntity {

    private String username;
    private String password;
    private String email;
    private String role;

    @Builder
    public LocalUserEntity(String username, String encryptedPassword, String email, String role) {
        this.username = username;
        this.password = encryptedPassword;
        this.email = email;
        this.role = role;
    }

}
