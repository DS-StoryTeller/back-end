package com.cojac.storyteller.user.jwt;

import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.user.entity.LocalUserEntity;
import com.cojac.storyteller.user.dto.security.CustomUserDetails;
import com.cojac.storyteller.common.util.ErrorResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        if (requestUri.matches("^\\/login(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }
        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = getAccessTokenFromRequest(request, response, filterChain);
        if (accessToken == null) return;

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        if (validateToken(response, accessToken)) return;

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        if (validateCategory(response, accessToken)) return;

        String authenticationMethod = jwtUtil.getAuthenticationMethod(accessToken);
        if (authenticationMethod.equals("local")) {
            // 자체 로그인 사용자 검증
            authenticateLocalUser(accessToken, request, response, filterChain);
        } else if (authenticationMethod.equals("social")) {
            // 소셜 로그인 사용자 검증
            authenticateSocialUser(accessToken, request, response, filterChain);
        } else {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }
    }

    private static String getAccessTokenFromRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = request.getHeader("access");
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return null;
        }
        return accessToken;
    }

    private boolean validateCategory(HttpServletResponse response, String accessToken) throws IOException {
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return true;
        }
        return false;
    }

    private boolean validateToken(HttpServletResponse response, String accessToken) throws IOException {
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return true;
        }
        return false;
    }

    private void authenticateLocalUser(String accessToken, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String username = jwtUtil.getUserKey(accessToken);
        String role = jwtUtil.getRole(accessToken);

        LocalUserEntity localUserEntity = LocalUserEntity.builder()
                .username(username)
                .encryptedPassword("password")
                .email("email")
                .role(role)
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(localUserEntity);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private void authenticateSocialUser(String accessToken, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        String accountId = jwtUtil.getUserKey(accessToken);
//        String role = jwtUtil.getRole(accessToken);
//
//        SocialUserDTO socialUserDTO = new SocialUserDTO(role, "", username);
//        //UserDetails에 회원 정보 객체 담기
//        CustomOAuth2User customOAuth2User = new CustomOAuth2User(socialUserDTO);
//
//        //스프링 시큐리티 인증 토큰 생성
//        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
//        //세션에 사용자 등록
//        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
