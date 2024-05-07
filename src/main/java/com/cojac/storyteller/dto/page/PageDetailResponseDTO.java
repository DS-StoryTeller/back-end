package com.cojac.storyteller.dto.page;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDetailResponseDTO {
    private Integer bookId;
    private Integer pageNumber;
    private String image;
    private String content;
}
