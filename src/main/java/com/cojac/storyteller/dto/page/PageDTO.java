package com.cojac.storyteller.dto.page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageDTO {

    private Integer id;
    private Integer page;
    private String image;
    private String content;
    private Integer bookId;
}
