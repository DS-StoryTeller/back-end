package com.cojac.storyteller.dto.page;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PageDTO {

    private Integer id;
    private Integer page;
    private String image;
    private String content;
    private Integer bookId;
}