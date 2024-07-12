package com.cojac.storyteller.jwt;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.LocalUserEntity;
import com.cojac.storyteller.dto.user.CustomUserDetails;
import com.cojac.storyteller.dto.user.oauth2.CustomOAuth2User;
import com.cojac.storyteller.dto.user.oauth2.SocialUserDTO;
import com.cojac.storyteller.util.ErrorResponseUtil;
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
        String accessToken = request.getHeader("access");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }

        // userKey, role 값을 획득
        String userKey = jwtUtil.getUserKey(accessToken);
        String role = jwtUtil.getRole(accessToken);

        // 자체로그인인지 소셜 로그인인지 구별
        String authenticationMethod = jwtUtil.getAuthenticationMethod(accessToken);
        if (authenticationMethod.equals("local")) {

            LocalUserEntity localUserEntity = new LocalUserEntity("password", userKey, role);
            //UserDetails에 회원 정보 객체 담기
            CustomUserDetails customUserDetails = new CustomUserDetails(localUserEntity);

            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } else if (authenticationMethod.equals("social")) {
            SocialUserDTO socialUserDTO = new SocialUserDTO(userKey, "", role);
            //UserDetails에 회원 정보 객체 담기
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(socialUserDTO);

            //스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
            //세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } else {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }
    }
}
