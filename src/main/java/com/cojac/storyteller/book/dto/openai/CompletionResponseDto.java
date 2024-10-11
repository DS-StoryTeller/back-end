package com.cojac.storyteller.book.dto.openai;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CompletionResponseDto {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;

    @Getter
    @NoArgsConstructor
    public static class Choice {
        private Message message;
        private int index;
        private Object logprobs;
        private String finish_reason;

        @Getter
        @NoArgsConstructor
        public static class Message {
            private String role;
            private String content;
        }
    }
}
