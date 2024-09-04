package com.cojac.storyteller.dto.user;

import com.cojac.storyteller.domain.SocialUserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class SocialUserDTO implements UserDTO {

    private String id;
    private String role;
    private String username; // 실제 유저 이름
    private String accountId;
    private String email; // 리소스 서버에서 받은 정보로 사용자의 특정 아이디값

    @Builder
    public SocialUserDTO(String accountId, String username, String role, String email) {
        this.role = role;
        this.username = username;
        this.accountId = accountId;
        this.email = email;
    }

    public static SocialUserDTO mapToUserDTO(SocialUserEntity socialUser) {
        return SocialUserDTO.builder()
                .accountId(socialUser.getAccountId())
                .role(socialUser.getRole())
                .username(socialUser.getUsername())
                .email(socialUser.getEmail())
                .build();
    }
}
