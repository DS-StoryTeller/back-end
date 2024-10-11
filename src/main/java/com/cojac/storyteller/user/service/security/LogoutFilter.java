package com.cojac.storyteller.user.service.security;

import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.response.code.ResponseCode;
import com.cojac.storyteller.response.dto.ResponseDTO;
import com.cojac.storyteller.user.dto.ReissueDTO;
import com.cojac.storyteller.user.jwt.JWTUtil;
import com.cojac.storyteller.user.repository.LocalUserRepository;
import com.cojac.storyteller.user.repository.SocialUserRepository;
import com.cojac.storyteller.common.redis.RedisService;
import com.cojac.storyteller.common.util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class LogoutFilter extends GenericFilterBean {

    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final LocalUserRepository localUserRepository;
    private final SocialUserRepository socialUserRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        // /logout 경로와 POST 메소드인지 확인
        if (!requestUri.equals("/logout") || !requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = getRefreshTokenFromRequest(request, response);
        if (refreshToken == null) return;

        // 토큰 만료 여부 확인
        if (validateToken(response, refreshToken)) return;

        // 토큰이 refresh인지 확인
        if (validateCategory(response, refreshToken)) return;

        String authenticationMethod = jwtUtil.getAuthenticationMethod(refreshToken);
        if (authenticationMethod.equals("local")) {
            authenticateLocalUser(request, response, authenticationMethod);
        } else if (authenticationMethod.equals("social")) {
            authenticateSocialUser(request, response, authenticationMethod);
        } else {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
            return;
        }
    }

    private static String getRefreshTokenFromRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = request.getHeader("refresh");
        if (refreshToken == null) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return null;
        }
        return refreshToken;
    }

    private void authenticateSocialUser(HttpServletRequest request, HttpServletResponse response, String authenticationMethod) throws IOException {
        String accountId = getUserKey(request, authenticationMethod);
        if(checkAccountId(response, accountId)) return;

        // Redis에 저장된 refresh 토큰 확인
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + accountId;
        if (checkTokenInRedis(refreshTokenKey, response)) return;

        // 로그아웃 처리: Redis에서 refresh 토큰 제거
        redisService.deleteValues(refreshTokenKey);

        // 응답 생성 및 전송
        ResponseDTO<?> responseDTO = new ResponseDTO<>(ResponseCode.SUCCESS_LOGOUT, null);
        writeJsonResponse(response, responseDTO);
    }
    private boolean checkAccountId(HttpServletResponse response, String accountId) throws IOException {
        if (!socialUserRepository.existsByAccountId(accountId)) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.USER_NOT_FOUND);
            return true;
        }
        return false;
    }

    private void authenticateLocalUser(HttpServletRequest request, HttpServletResponse response, String authenticationMethod) throws IOException {
        String username = getUserKey(request, authenticationMethod);
        if (checkUsername(response, username)) return;

        // Redis에 저장된 refresh 토큰 확인
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + username;
        if (checkTokenInRedis(refreshTokenKey, response)) return;

        // 로그아웃 처리: Redis에서 refresh 토큰 제거
        redisService.deleteValues(refreshTokenKey);

        // 응답 생성 및 전송
        ResponseDTO<?> responseDTO = new ResponseDTO<>(ResponseCode.SUCCESS_LOGOUT, null);
        writeJsonResponse(response, responseDTO);
    }

    private boolean checkUsername(HttpServletResponse response, String username) throws IOException {
        if (!localUserRepository.existsByUsername(username)) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.USER_NOT_FOUND);
            return true;
        }
        return false;
    }

    private boolean validateCategory(HttpServletResponse response, String refreshToken) throws IOException {
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return true;
        }
        return false;
    }

    private boolean validateToken(HttpServletResponse response, String refreshToken) throws IOException {
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return true;
        }
        return false;
    }

    private String getUserKey(HttpServletRequest request, String authenticationMethod) throws IOException {
        ReissueDTO reissueDTO = objectMapper.readValue(request.getReader(), ReissueDTO.class);
        return authenticationMethod.equals("local") ? reissueDTO.getUsername() : reissueDTO.getAccountId();
    }

    private boolean checkTokenInRedis(String refreshTokenKey, HttpServletResponse response) throws IOException {
        if (!redisService.checkExistsValue(refreshTokenKey)) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
            return true;
        }
        return false;
    }

    private void writeJsonResponse(HttpServletResponse response, Object responseObject) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String jsonResponse = objectMapper.writeValueAsString(responseObject);
        response.getWriter().write(jsonResponse);
    }
}
