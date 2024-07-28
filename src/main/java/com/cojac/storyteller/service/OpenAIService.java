package com.cojac.storyteller.service;

import com.cojac.storyteller.dto.openai.CompletionRequestDto;
import com.cojac.storyteller.dto.openai.CompletionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.secret-key}")
    private String apiKey;

    @PostConstruct
    private void init() {
        httpHeaders.set("Authorization", "Bearer " + apiKey);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public String generateStory(String prompt, Integer age) {
        String url = "https://api.openai.com/v1/chat/completions";
        CompletionRequestDto.Message message = CompletionRequestDto.Message.builder()
                .role("user")
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
                .content(story + "라는 동화 내용이 있어. 이 내용에 대해 창의력을 향상시킬 수 있는 질문 1개를 한국어 존댓말로 알려줘."
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

    public byte[] generateImage(String prompt) {
        String url = "https://api.openai.com/v1/images/generate";

        Map<String, Object> requestDto = Map.of(
                "prompt", prompt,
                "size", "1024x1024" // 생성할 이미지의 크기
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestDto, httpHeaders);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        if (response.getBody() != null) {
            String imageUrl = (String) response.getBody().get("url");
            return downloadImage(imageUrl); // 이미지 URL로부터 이미지를 다운로드합니다.
        }
        return null;
    }

    private byte[] downloadImage(String imageUrl) {
        try {
            return restTemplate.getForObject(imageUrl, byte[].class);
        } catch (Exception e) {
            // 에러 메세지
            return null;
        }
    }
}
