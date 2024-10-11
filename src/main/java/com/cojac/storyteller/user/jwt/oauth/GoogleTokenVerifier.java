package com.cojac.storyteller.user.jwt.oauth;

import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.user.dto.oauth.GoogleLoginRequestDTO;
import com.cojac.storyteller.user.exception.InvalidIdTokenException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    @Value("${google.client.id}")
    private String CLIENT_ID;

    public GoogleIdToken.Payload verifyIdToken(GoogleLoginRequestDTO googleLoginRequestDTO) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        // ID 토큰 검증
        GoogleIdToken idToken = verifier.verify(googleLoginRequestDTO.getIdToken());
        if (idToken != null) {
            return idToken.getPayload(); // 사용자 정보 반환
        } else {
            throw new InvalidIdTokenException(ErrorCode.INVALID_ID_TOKEN);
        }
    }
}
