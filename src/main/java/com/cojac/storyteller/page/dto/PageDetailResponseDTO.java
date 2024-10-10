package com.cojac.storyteller.page.dto;

import com.cojac.storyteller.unknownWord.dto.UnknownWordDTO;
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
    private List<UnknownWordDTO> unknownWords;
}
