package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue("L")
public class LocalUserEntityEntity extends UserEntity {

    private String username;

    private String password;

    private String role;

    public LocalUserEntityEntity(String encryptedPassword, String username, String role) {
        this.password = encryptedPassword;
        this.username = username;
        this.role = role;
    }

}
