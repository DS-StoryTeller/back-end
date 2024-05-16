package com.cojac.storyteller.dto.book;

import com.cojac.storyteller.dto.page.PageDTO;
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
