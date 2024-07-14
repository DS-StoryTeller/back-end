package com.cojac.storyteller.dto.unknownWord;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnknownWordDetailDto {
    private Integer bookId;
    private Integer pageId;
    private Integer unknownwordId;
    private String unknownWord;
    private Integer position;
}
