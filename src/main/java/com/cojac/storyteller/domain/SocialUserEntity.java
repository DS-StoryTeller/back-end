package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue("S")
public class SocialUserEntity extends UserEntity {

    private Integer id;
    private String accountId; // 사용자를 식별하는 아이디 (소셜명 + 특정 아이디값)
    private String username; // 사용자 이름
    private String email;
    private String role;
}
