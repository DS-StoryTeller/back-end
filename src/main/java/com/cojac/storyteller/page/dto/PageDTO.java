package com.cojac.storyteller.page.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PageDTO {

    private Integer id;
    private Integer pageNumber;
    private String image;
    private String content;
    private Integer bookId;
}