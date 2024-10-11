package com.cojac.storyteller.user.dto;

import com.cojac.storyteller.user.entity.SocialUserEntity;
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
    private String nickname;
    private String accountId;
    private String email;

    public static SocialUserDTO mapToSocialUserDTO(SocialUserEntity socialUser) {
        return SocialUserDTO.builder()
                .id(socialUser.getId())
                .role(socialUser.getRole())
                .accountId(socialUser.getAccountId())
                .nickname(socialUser.getNickname())
                .email(socialUser.getEmail())
                .build();
    }

}
