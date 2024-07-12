package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.SocialUserEntity;
import com.cojac.storyteller.dto.user.oauth2.*;
import com.cojac.storyteller.exception.UserNotFoundException;
import com.cojac.storyteller.repository.SocialUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialUserRepository socialUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2ResponseFactory 객체를 생성
        OAuth2ResponseFactory factory = new OAuth2ResponseFactory();
        // registrationId와 attributes를 이용하여 OAuth2Response 객체를 생성
        OAuth2Response oAuth2Response = factory.createOAuth2Response(registrationId, oAuth2User.getAttributes());

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만들기
        String accountId = oAuth2Response.getProvider() + ":" + oAuth2Response.getProviderId();
        Optional<SocialUserEntity> existDataOptional = socialUserRepository.findByAccountId(accountId);

        // DB에 없는 사용자라면 회원가입처리 및 DTO 응답
        if (!existDataOptional.isPresent()) {
            SocialUserEntity socialUserEntity = new SocialUserEntity(accountId, oAuth2Response.getUserName(), oAuth2Response.getEmail(), "ROLE_USER");
            socialUserRepository.save(socialUserEntity);

            SocialUserDTO socialUserDTO = new SocialUserDTO(socialUserEntity.getId(), accountId, oAuth2Response.getUserName(), "ROLE_USER");

            return new CustomOAuth2User(socialUserDTO);

        } else { // 기존 SocialUserEntity 수정 및 DTO 응답
            SocialUserEntity existData = existDataOptional.get();
            existData.setEmail(oAuth2Response.getEmail());
            existData.setUsername(oAuth2Response.getUserName());

            SocialUserDTO socialUserDTO = new SocialUserDTO(existData.getId(), existData.getAccountId(), oAuth2Response.getUserName(), existData.getRole());

            return new CustomOAuth2User(socialUserDTO);
        }
    }
}


