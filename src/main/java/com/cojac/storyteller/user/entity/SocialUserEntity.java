package com.cojac.storyteller.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue("S")
public class SocialUserEntity extends UserEntity {

    private Integer id;
    private String accountId; // 사용자를 식별하는 아이디 (소셜명 + 특정 아이디값)
    private String nickname; // 사용자 이름
    private String email;
    private String role;

    @Builder
    public SocialUserEntity(String accountId, String nickname, String email, String role) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateUsername(String nickname) {
        this.nickname = nickname;
    }
}
