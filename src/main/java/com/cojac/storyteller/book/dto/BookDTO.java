package com.cojac.storyteller.book.dto;

import com.cojac.storyteller.page.dto.PageDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BookDTO {

    private Integer id;
    private String title;
    private String coverImage;
    private Integer currentPage;
    private List<PageDTO> pages;
    private Integer profileId;
    private Integer totalPageCount;
    private Boolean isReading;
    private Boolean isFavorite;
}