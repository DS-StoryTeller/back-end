package com.cojac.storyteller.dto.book;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookDTO {

    private Integer id;
    private String title;
    private String coverImage;
    private Integer currentPage;
}
