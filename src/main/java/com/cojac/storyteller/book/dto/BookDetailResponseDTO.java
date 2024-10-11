package com.cojac.storyteller.book.dto;

import com.cojac.storyteller.page.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDetailResponseDTO {
    private Integer bookId;
    private String title;
    private String coverImage;
    private Integer currentPage;
    private Integer totalPageCount;
    private List<PageDTO> pages;
}
