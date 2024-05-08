package com.cojac.storyteller.dto.unknownWord;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnknownWordDetailDto {
    private Integer bookId;
    private Integer pageId;
    private String unknownWord;
    private Integer position;
}
