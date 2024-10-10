package com.cojac.storyteller.common.openAI;

import com.cojac.storyteller.book.dto.openai.CompletionRequestDto;
import com.cojac.storyteller.book.dto.openai.CompletionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${openai.secret-key}")
    private String apiKey;
    @Value("${openai.model}")
    private String model;

    public String generateStory(String prompt, Integer age) {
        String url = "https://api.openai.com/v1/chat/completions";
        CompletionRequestDto.Message message = CompletionRequestDto.Message.builder()
                .role("user")
                // 제목과 내용을 Title: 과 Content: 로 구분하여 요청
                .content("Generate a story with the following theme: " + prompt + ". Provide the response in the following format:\n\nTitle: [Your Title]\n\nContent: [Your Content]. " +
                        "Please generate an English fairy tale suitable for the difficulty level appropriate for " + age + " years old." +
                        "Please write at least 10 paragraphs"
                )
                .build();
        CompletionRequestDto requestDto = CompletionRequestDto.builder()
                .model(model)
                .messages(Collections.singletonList(message))
                .temperature(0.8f)
                .build();

        HttpEntity<CompletionRequestDto> requestEntity = new HttpEntity<>(requestDto, httpHeaders);
        ResponseEntity<CompletionResponseDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, CompletionResponseDto.class);

        if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
            return response.getBody().getChoices().get(0).getMessage().getContent();
        }
        return null;
    }

    public String generateQuiz(String story, Integer age) {
        String url = "https://api.openai.com/v1/chat/completions";
        CompletionRequestDto.Message message = CompletionRequestDto.Message.builder()
                .role("user")
                // 퀴즈 3개를 \n으로 구분하여 요청
                .content(story + "라는 동화 내용이 있어. 이 내용에 대해 창의력을 향상시킬 수 있는 질문 1개를 한국어 존댓말로 알려줘. "
                )
                .build();
        CompletionRequestDto requestDto = CompletionRequestDto.builder()
                .model(model)
                .messages(Collections.singletonList(message))
                .temperature(0.8f)
                .build();

        HttpEntity<CompletionRequestDto> requestEntity = new HttpEntity<>(requestDto, httpHeaders);
        ResponseEntity<CompletionResponseDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, CompletionResponseDto.class);

        if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
            return response.getBody().getChoices().get(0).getMessage().getContent();
        }
        return null;
    }

    /**
     * DALL-E API를 호출하여 이미지를 생성하고, base64로 인코딩된 이미지를 바이트 배열로 반환
     * @param prompt 이미지 생성에 사용할 프롬프트
     * @return base64로 인코딩된 이미지의 바이트 배열
     */
    public byte[] generateImage(String prompt) {
        String url = "https://api.openai.com/v1/images/generations";

        Map<String, Object> requestDto = Map.of(
                "prompt", prompt,
                "size", "1024x1024",
                "response_format", "b64_json" // 응답 형식 (base64 JSON)
        );

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<Map> response;
        try {
            // DALL-E API 호출
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        } catch (HttpClientErrorException e) {
            // HTTP 클라이언트 오류 처리
            System.out.println("OpenAI API 호출 오류 (HTTP 상태 코드): " + e.getStatusCode());
            System.out.println("오류 메시지: " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.out.println("OpenAI API 호출 오류: " + e.getMessage());
            return null;
        }

        // API 응답 처리
        if (response.getBody() != null) {
            // 응답 데이터에서 base64 인코딩된 이미지 추출
            Map<String, String> data = (Map<String, String>) ((List<?>) response.getBody().get("data")).get(0);
            if (data != null) {
                String base64Image = data.get("b64_json");
                // base64 문자열을 디코딩하여 바이트 배열로 변환
                return Base64.getDecoder().decode(base64Image);
            }
        }
        return null;
    }
}