package com.cojac.storyteller.jwt;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.domain.RefreshEntity;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.user.CustomUserDetails;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.repository.RefreshRedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JWTUtil jwtUtil;

    private final RefreshRedisRepository refreshRedisRepository;

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

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 86400000L); // 추후에 변경 -> 600000L
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // refresh 토큰 저장
        addRefreshEntity(refresh, username);

        // access 토큰 설정
        response.setHeader("access", access);

        // 로그인 성공시 body에 응답 정보 담기
        UserDTO userDTO = new UserDTO(userId, username, role);
        // body에 refresh 토큰 설정
        userDTO.setRefreshToken(refresh);
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>(ResponseCode.SUCCESS_LOGIN, userDTO);

        // 응답의 Content-Type 및 Character Encoding 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 라이브러리 사용 (예: Jackson)
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);

        // 응답 본문에 JSON 데이터 쓰기
        response.getWriter().write(jsonResponse);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
    }

    private void addRefreshEntity(String refresh, String username) {
        RefreshEntity refreshEntity = new RefreshEntity(refresh, username);
        refreshRedisRepository.save(refreshEntity);
    }
}
