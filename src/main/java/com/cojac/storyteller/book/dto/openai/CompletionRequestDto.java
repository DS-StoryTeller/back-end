package com.cojac.storyteller.book.dto.openai;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletionRequestDto {
    private String model;
    private List<Message> messages;
    private float temperature;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}
