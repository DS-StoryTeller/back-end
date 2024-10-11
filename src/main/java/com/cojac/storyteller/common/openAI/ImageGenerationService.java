package com.cojac.storyteller.common.openAI;

import com.cojac.storyteller.common.amazon.AmazonS3Service;
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
        String prompt = "Create a whimsical and enchanting book cover image for a children's storybook titled \"" + bookTitle + "\". The cover should feature bright, vibrant colors and a playful, animated style. It should capture the magical and imaginative essence of the story, with charming characters and whimsical elements that appeal to young readers. Ensure the design is eye-catching and evokes a sense of wonder and fun.";

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
        String prompt = "Create a charming and whimsical illustration based on the following content: \"" + pageContent + "\". The image should be in a cute, animated style with bright, vibrant colors. It should capture the essence of the content and be visually engaging for young readers, with playful and imaginative elements that bring the scene to life. Please ensure that the illustration does not include any text or titles, focusing solely on the visual representation.";

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
            String dirPath = System.getProperty("user.dir") + "/books/photos";
            return amazonS3Service.uploadImageToS3(imageBytes, dirPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to S3.", e);
        }
    }

}
