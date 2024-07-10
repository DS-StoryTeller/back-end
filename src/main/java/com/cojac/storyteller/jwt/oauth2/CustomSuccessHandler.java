package com.cojac.storyteller.jwt.oauth2;

import com.cojac.storyteller.dto.user.oauth2.CustomOAuth2User;
import com.cojac.storyteller.jwt.JWTUtil;
import com.cojac.storyteller.service.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final long REFRESH_TOKEN_EXPIRATION = 1209600000L; // 14 days

    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, IOException {

        //OAuth2User 정보 가져오기
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String accountId = customUserDetails.getAccountId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String refresh = jwtUtil.createJwt("social", "refresh", accountId, role, REFRESH_TOKEN_EXPIRATION);

        // Redis 키값
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + accountId;

        // Refresh 토큰 Redis에 저장
        redisService.setValues(refreshTokenKey, refresh);

        response.addCookie(createCookie("refresh", refresh));
        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 쿠키 24시간동안 유지
        //cookie.setSecure(true); // HTTPS 인 경우
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}