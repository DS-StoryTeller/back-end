package com.cojac.storyteller.unknownWord.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnknownWordRequestDTO {
    private Integer profileId;
    private Integer bookId;
    private Integer pageNum;
    private String unknownWord;
    private Integer position;
}
