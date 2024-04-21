package com.cojac.storyteller.jwt;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.domain.RefreshEntity;
import com.cojac.storyteller.dto.response.ErrorResponseDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.exception.RequestParsingException;
import com.cojac.storyteller.repository.RefreshRedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;

    private final RefreshRedisRepository refreshRedisRepository;

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
            sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }

        // DB에 저장되어 있는지 확인
        Optional<RefreshEntity> isExist = refreshRedisRepository.findById(refreshToken);
        if (isExist.isEmpty()) {
            sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 redis에서 제거
        refreshRedisRepository.deleteById(refreshToken);

        // response
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>(ResponseCode.SUCCESS_LOGOUT, null);

        // 응답의 Content-Type 및 Character Encoding 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 라이브러리 사용 (예: Jackson)
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);

        // 응답 본문에 JSON 데이터 쓰기
        response.getWriter().write(jsonResponse);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        // 에러 메시지를 JSON 형식으로 직렬화
        ObjectMapper objectMapper = new ObjectMapper();

        // LocalDateTime 직렬화를 위한 JavaTimeModule 생성
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 원하는 날짜 및 시간 형식 지정 (예: "yyyy-MM-dd HH:mm:ss")
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // LocalDateTimeSerializer 및 LocalDateTimeDeserializer를 설정
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        // LocalDateTime를 JSON 형식으로 직렬화를 위한 설정
        objectMapper.registerModule(javaTimeModule);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(errorCode);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        // 응답의 Content-Type 및 Character Encoding 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 응답의 HTTP 상태 코드 설정
        response.setStatus(errorCode.getStatus().value());

        // 응답 본문에 JSON 데이터 작성
        PrintWriter writer = response.getWriter();
        writer.print(jsonResponse);
        writer.flush();
    }
}
