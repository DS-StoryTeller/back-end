package com.cojac.storyteller.book.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class BookListResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
