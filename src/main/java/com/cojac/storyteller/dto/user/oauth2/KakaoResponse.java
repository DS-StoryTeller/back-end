package com.cojac.storyteller.dto.user.oauth2;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;
    private Map<String, Object> attributeAccount;
    private Map<String, Object> attributeProfile;

    public KakaoResponse(Map<String, Object> attribute) {
         /*
        System.out.println(attribute);
            {id=아이디값,
            connected_at=2022-02-22T15:50:21Z,
            properties={nickname=이름},
            kakao_account={
                profile_nickname_needs_agreement=false,
                profile={nickname=이름},
                has_email=true,
                email_needs_agreement=false,
                is_email_valid=true,
                is_email_verified=true,
                email=이메일}
            }
        */

        this.attribute = attribute;
        this.attributeAccount = (Map<String, Object>) attribute.get("kakao_account");
        this.attributeProfile = (Map<String, Object>) attributeAccount.get("profile");
    }


    @Override
    public String getProvider() {
        return "Kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attributeAccount.get("email").toString();
    }

    @Override
    public String getUserName() {
        return attributeProfile.get("nickname").toString();
    }
}
