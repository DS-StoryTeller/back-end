package com.cojac.storyteller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SocialUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String accountId; // 사용자를 식별하는 아이디 (소셜명 + 특정 아이디값)

    private String username; // 사용자 이름

    private String email;

    private String role;

    public SocialUserEntity(String accountId, String username, String email, String role) {
        this.accountId = accountId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
