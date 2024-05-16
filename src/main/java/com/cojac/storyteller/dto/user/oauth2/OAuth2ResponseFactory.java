package com.cojac.storyteller.dto.user.oauth2;

import java.util.Map;

/**
 * OAuth2ResponseFactory 클래스는 OAuth2Response 객체를 생성하는 팩토리입니다.
 */
public class OAuth2ResponseFactory {

    public OAuth2Response createOAuth2Response(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equals("google")) {
            return new GoogleResponse(attributes);
        } else if (registrationId.equals("kakao")) {
            return new KakaoResponse(attributes);
        } else {
            return null;
        }
    }
}
