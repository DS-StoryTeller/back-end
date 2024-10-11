package com.cojac.storyteller.common.util;

import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.response.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorResponseUtil {

    public static void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException, IOException {
        // 에러 메시지를 JSON 형식으로 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
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
