package com.cojac.storyteller.jwt;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.domain.RefreshEntity;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.dto.user.UsernameDTO;
import com.cojac.storyteller.exception.RequestParsingException;
import com.cojac.storyteller.repository.RefreshRedisRepository;
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
import java.util.Optional;

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
        // 경로와 HTTP Method 유효한지
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = request.getHeader("refresh");
        if (refreshToken == null) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }

        // 본문에서 사용자 이름 추출
        String username = getUsername(request);
        if (username == null || username.isEmpty()) {
            ErrorResponseUtil.sendErrorResponse(response, ErrorCode.BAD_REQUEST);
            return;
        }

        // Redis 키값
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + username;

        // Redis에 저장되어 있는지 확인
        String values = redisService.getValues(refreshTokenKey);
        if (values == null || values.isEmpty()) {
            throw new RequestParsingException(ErrorCode.TOKEN_EXPIRED);
        }

        //로그아웃 진행
        //Refresh 토큰 redis에서 제거
        redisService.deleteValues(refreshTokenKey);

        // response
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>(ResponseCode.SUCCESS_LOGOUT, null);

        writeJsonResponse(response, responseDTO);
    }

    private String getUsername(HttpServletRequest request) throws IOException {
        UsernameDTO usernameDTO = objectMapper.readValue(request.getReader(), UsernameDTO.class);
        String username = usernameDTO.getUsername();
        return username;
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
