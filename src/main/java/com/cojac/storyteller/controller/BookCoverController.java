package com.cojac.storyteller.controller;

import com.cojac.storyteller.service.BookCoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookCoverController {

    private final BookCoverService bookCoverService;

    @PostMapping("/bookcover")
    public ResponseEntity<String> createBookCover(@RequestBody BookCoverRequest request) {
        try {
            // 동화에 어울리는 책 이미지 커버를 생성하고, S3에 업로드합니다.
            String filePath = bookCoverService.createAndUploadBookCover(request.getStoryPrompt());
            return ResponseEntity.ok("Image saved at: " + filePath);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to generate book cover: " + e.getMessage());
        }
    }

    static class BookCoverRequest {
        private String storyPrompt;

        public String getStoryPrompt() {
            return storyPrompt;
        }

        public void setStoryPrompt(String storyPrompt) {
            this.storyPrompt = storyPrompt;
        }
    }
}
