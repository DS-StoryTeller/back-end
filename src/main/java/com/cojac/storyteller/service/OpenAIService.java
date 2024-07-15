package com.cojac.storyteller.service;

import com.cojac.storyteller.dto.openai.CompletionRequestDto;
import com.cojac.storyteller.dto.openai.CompletionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

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
}
