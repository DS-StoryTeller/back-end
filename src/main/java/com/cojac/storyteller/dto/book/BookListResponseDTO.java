package com.cojac.storyteller.dto.book;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookListResponseDTO {
    private Integer bookId;
    private String title;
    private String coverImage;
    private Integer currentPage;
    private Boolean isReading;
    private Boolean isFavorite;

    public BookListResponseDTO(Integer bookId, String title, String coverImage, Integer currentPage, Boolean isReading, Boolean isFavorite) {
        this.bookId = bookId;
        this.title = title;
        this.coverImage = coverImage;
        this.currentPage = currentPage;
        this.isReading = isReading;
        this.isFavorite = isFavorite;
    }
}
