package com.cojac.storyteller.unknownWord.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnknownWordDetailDTO {
    private Integer bookId;
    private Integer pageId;
    private Integer unknownwordId;
    private String unknownWord;
    private Integer position;
}
