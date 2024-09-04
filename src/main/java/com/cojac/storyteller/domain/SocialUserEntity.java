package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue("S")
public class SocialUserEntity extends UserEntity {

    private String id;
    private String accountId; // 사용자를 식별하는 아이디 (소셜명 + 특정 아이디값)
    private String username; // 사용자 이름
    private String email;
    private String role;

    @Builder
    public SocialUserEntity(String id, String accountId, String username, String email, String role) {
        this.id = id;
        this.accountId = accountId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateUsername(String username) {
        this.username = username;
    }
}
