package com.cojac.storyteller.dto.user.oauth2;

import com.cojac.storyteller.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialUserDTO implements UserDTO {

    private Integer id;
    private String role;
    private String username; // 실제 유저 이름
    private String accountId; // 리소스 서버에서 받은 정보로 사용자의 특정 아이디값

    public SocialUserDTO(Integer id, String accountId, String username, String role) {
        this.id = id;
        this.role = role;
        this.username = username;
        this.accountId = accountId;
    }

    public SocialUserDTO(String accountId, String username, String role) {
        this.role = role;
        this.username = username;
        this.accountId = accountId;
    }
}
