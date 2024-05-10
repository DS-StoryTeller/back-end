package com.cojac.storyteller.service;

import com.cojac.storyteller.domain.SocialUserEntity;
import com.cojac.storyteller.dto.user.oauth2.CustomOAuth2User;
import com.cojac.storyteller.dto.user.oauth2.GoogleResponse;
import com.cojac.storyteller.dto.user.oauth2.OAuth2Response;
import com.cojac.storyteller.dto.user.oauth2.SocialUserDTO;
import com.cojac.storyteller.repository.SocialUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialUserRepository socialUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            // 카카오용 응답 처리 추가
        } else {
            return null;
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만들기
        String accountId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        SocialUserEntity existData = socialUserRepository.findByAccountId(accountId);

        // 새롭게 SocialUserEntity 생성 및 DTO 응답
        if(existData == null) {
            SocialUserEntity socialUserEntity
                    = new SocialUserEntity(accountId, oAuth2Response.getUserName(), oAuth2Response.getEmail(), "ROLE_USER");

            socialUserRepository.save(socialUserEntity);

            SocialUserDTO socialUserDTO
                    = new SocialUserDTO("ROLE_USER", oAuth2Response.getUserName(), accountId);

            return new CustomOAuth2User(socialUserDTO);

        } else { // 기존 SocialUserEntity 수정 및 DTO 응답
            existData.setEmail(oAuth2Response.getEmail());
            existData.setUsername(oAuth2Response.getUserName());

            SocialUserDTO socialUserDTO
                    = new SocialUserDTO(existData.getRole(), oAuth2Response.getUserName(), existData.getUsername());

            return new CustomOAuth2User(socialUserDTO);
        }
    }
}


