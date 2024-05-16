package com.cojac.storyteller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageRequestDTO {
    private Integer profileId;
    private Integer bookId;
    private Integer pageNum;
}
