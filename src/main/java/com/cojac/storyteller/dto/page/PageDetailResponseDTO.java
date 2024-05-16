package com.cojac.storyteller.dto.page;

import com.cojac.storyteller.dto.unknownWord.UnknownWordDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDetailResponseDTO {
    private Integer pageId;
    private Integer pageNumber;
    private String image;
    private String content;
    // unknownWord
    private List<UnknownWordDto> unknownWords;
}
