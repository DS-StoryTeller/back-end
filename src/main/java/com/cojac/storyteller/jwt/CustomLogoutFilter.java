package com.cojac.storyteller.jwt;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.user.ReissueDTO;
import com.cojac.storyteller.exception.RequestParsingException;
import com.cojac.storyteller.service.RedisService;
import com.cojac.storyteller.util.ErrorResponseUtil;
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
public class CustomLogoutFilter extends GenericFilterBean {

    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

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

        String refreshToken = request.getHeader("refresh");
        if (refreshToken == null) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return;
        }

        try {
            // 토큰 만료 여부 확인
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return;
        }

        // 토큰이 refresh인지 확인
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }

        String authenticationMethod = jwtUtil.getAuthenticationMethod(refreshToken);
        String userKey = getUserKey(request, authenticationMethod);

        // Redis에 저장된 refresh 토큰 확인
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + userKey;
        checkTokenInRedis(refreshTokenKey);

        // 로그아웃 처리: Redis에서 refresh 토큰 제거
        redisService.deleteValues(refreshTokenKey);

        // 응답 생성 및 전송
        ResponseDTO<?> responseDTO = new ResponseDTO<>(ResponseCode.SUCCESS_LOGOUT, null);
        writeJsonResponse(response, responseDTO);
    }

    private String getUserKey(HttpServletRequest request, String authenticationMethod) throws IOException {
        ReissueDTO reissueDTO = objectMapper.readValue(request.getReader(), ReissueDTO.class);
        return authenticationMethod.equals("local") ? reissueDTO.getUsername() : reissueDTO.getAccountId();
    }

    private void checkTokenInRedis(String refreshTokenKey) {
        String values = redisService.getValues(refreshTokenKey);
        if (values == null || values.isEmpty()) {
            throw new RequestParsingException(ErrorCode.TOKEN_EXPIRED);
        }
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
