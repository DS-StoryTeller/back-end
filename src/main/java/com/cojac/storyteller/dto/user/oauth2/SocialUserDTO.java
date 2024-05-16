package com.cojac.storyteller.dto.user.oauth2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserDTO {

    private String role;
    private String username; // 실제 유저 이름
    private String accountId; // 리소스 서버에서 받은 정보로 사용자의 특정 아이디값

}
