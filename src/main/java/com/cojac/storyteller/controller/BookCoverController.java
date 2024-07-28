package com.cojac.storyteller.controller;

import com.cojac.storyteller.service.BookCoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookCoverController {

    private final BookCoverService bookCoverService;

    @PostMapping("/bookcover")
    public ResponseEntity<String> createBookCover(@RequestBody BookCoverRequest request) {
        try {
            String imageUrl = bookCoverService.createAndUploadBookCover(request.getStoryPrompt());
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to generate book cover");
        }
    }
}

class BookCoverRequest {
    private String storyPrompt;

    public String getStoryPrompt() {
        return storyPrompt;
    }

    public void setStoryPrompt(String storyPrompt) {
        this.storyPrompt = storyPrompt;
    }
}
