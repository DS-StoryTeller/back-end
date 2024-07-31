package com.cojac.storyteller.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final OpenAIService openAIService;
    private final AmazonS3Service amazonS3Service;

    /**
     * 책 표지 이미지 생성 및 업로드
     * @param bookTitle 책 제목
     * @return 업로드된 이미지 URL
     */
    public String generateAndUploadBookCoverImage(String bookTitle) {
        String prompt = "Create a book cover image for a book titled \"" + bookTitle + "\". The cover should be visually appealing and relevant to the book's theme.";
        byte[] imageBytes = openAIService.generateImage(prompt);

        if (imageBytes == null) {
            throw new RuntimeException("Failed to generate image for book cover.");
        }

        return uploadImage(imageBytes);
    }

    /**
     * 페이지 이미지 생성 및 업로드
     * @param pageContent 페이지 내용
     * @return 업로드된 이미지 URL
     */
    public String generateAndUploadPageImage(String pageContent) {
        String prompt = "Create an illustration based on the following content: \"" + pageContent + "\". The image should capture the essence of the content and be visually engaging.";
        byte[] imageBytes = openAIService.generateImage(prompt);

        if (imageBytes == null) {
            throw new RuntimeException("Failed to generate image for page.");
        }

        return uploadImage(imageBytes);
    }

    /**
     * 이미지 바이트 배열을 S3에 업로드하고 URL을 반환
     * @param imageBytes 이미지 바이트 배열
     * @return 업로드된 이미지 URL
     */
    private String uploadImage(byte[] imageBytes) {
        try {
            return amazonS3Service.uploadImageToS3(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to S3.", e);
        }
    }
}
