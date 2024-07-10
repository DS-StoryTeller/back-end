package com.cojac.storyteller.jwt;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.response.ErrorResponseDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.user.CustomUserDetails;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final long ACCESS_TOKEN_EXPIRATION = 86400000L; // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION = 1209600000L; // 14 days

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //username, password 검증하기 위해 token에 담기
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        //UserDetails
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // 정보 가져오기(id, username, role)
        String username = customUserDetails.getUsername();
        Integer userId = customUserDetails.getId();
        String role = extractRole(authentication.getAuthorities());

        //토큰 생성
        String accessToken = jwtUtil.createJwt("self", "access", username, role, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = jwtUtil.createJwt("self", "refresh", username, role, REFRESH_TOKEN_EXPIRATION);

        // refresh 토큰 저장
        redisService.setValues(REFRESH_TOKEN_PREFIX + username, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRATION));

        // access 토큰 설정
        response.setHeader("access", accessToken);

        // 로그인 성공시 body에 응답 정보 담기
        UserDTO userDTO = new UserDTO(userId, username, role);
        userDTO.setRefreshToken(refreshToken);
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>(ResponseCode.SUCCESS_LOGIN, userDTO);

        writeResponse(response, responseDTO);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        // 로그인 실패시 401 응답 코드 반환
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // ErrorResponseDTO 생성
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorCode.UNAUTHORIZED, failed.getMessage());

        writeErrorResponse(response, errorResponse);
    }

    private void writeResponse(HttpServletResponse response, ResponseDTO<UserDTO> responseDTO) throws IOException {
        writeJsonResponse(response, responseDTO);
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorResponseDTO errorResponseDTO) throws IOException {
        writeJsonResponse(response, errorResponseDTO);
    }

    private void writeJsonResponse(HttpServletResponse response, Object responseObject) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String jsonResponse = objectMapper.writeValueAsString(responseObject);
        response.getWriter().write(jsonResponse);
    }

    private String extractRole(Collection<? extends GrantedAuthority> authorities) {
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        return iterator.hasNext() ? iterator.next().getAuthority() : "";
    }
}
