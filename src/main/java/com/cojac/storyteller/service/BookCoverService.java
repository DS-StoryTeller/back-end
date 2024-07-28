package com.cojac.storyteller.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BookCoverService {

    private final OpenAIService openAIService;
    private final AmazonS3Service amazonS3Service;

    /**
     * 동화에 어울리는 책 이미지 커버를 생성하고, S3에 업로드합니다.
     * @param storyPrompt 이미지 생성에 사용할 프롬프트
     * @return S3에 업로드된 이미지의 URL
     */
    public String createAndUploadBookCover(String storyPrompt) throws IOException {
        byte[] imageBytes = openAIService.generateImage(storyPrompt);
        if (imageBytes == null) {
            throw new RuntimeException("Failed to generate image");
        }
        return amazonS3Service.uploadImageToS3(imageBytes);
    }
}
