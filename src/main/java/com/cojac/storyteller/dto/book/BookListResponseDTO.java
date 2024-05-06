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
}
