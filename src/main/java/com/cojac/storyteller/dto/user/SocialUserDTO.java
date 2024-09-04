package com.cojac.storyteller.dto.user;

import com.cojac.storyteller.domain.SocialUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserDTO implements UserDTO {

    private Integer id;
    private String role;
    private String username;
    private String accountId;
    private String email;

    public static SocialUserDTO mapToSocialUserDTO(SocialUserEntity socialUser) {
        return SocialUserDTO.builder()
                .id(socialUser.getId())
                .role(socialUser.getRole())
                .accountId(socialUser.getAccountId())
                .username(socialUser.getUsername())
                .email(socialUser.getEmail())
                .build();
    }
}
