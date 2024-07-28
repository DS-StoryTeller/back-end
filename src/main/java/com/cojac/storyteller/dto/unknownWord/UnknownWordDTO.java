package com.cojac.storyteller.dto.unknownWord;

import com.cojac.storyteller.domain.UnknownWordEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnknownWordDTO {
    private Integer unknownWordId;
    private String unknownWord;
    private Integer position;

    public static List<UnknownWordDTO> toDto(List<UnknownWordEntity> unknownWordEntities) {
        List<UnknownWordDTO> unknownWordDTOS = new ArrayList<>();
        for(UnknownWordEntity unKnownWord : unknownWordEntities) {
            UnknownWordDTO unknownWordDto = new UnknownWordDTO(unKnownWord.getId(), unKnownWord.getUnknownWord(), unKnownWord.getPosition());
            unknownWordDTOS.add(unknownWordDto);
        }
        return unknownWordDTOS;
    }

}
