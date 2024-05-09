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
public class UnknownWordDto {
    private String unknownWord;
    private Integer position;

    public static List<UnknownWordDto> toDto(List<UnknownWordEntity> unknownWordEntities) {
        List<UnknownWordDto> unknownWordDtos = new ArrayList<>();
        for(UnknownWordEntity unKnownWord : unknownWordEntities) {
            UnknownWordDto unknownWordDto = new UnknownWordDto(unKnownWord.getUnknownWord(), unKnownWord.getPosition());
            unknownWordDtos.add(unknownWordDto);
        }
        return unknownWordDtos;
    }

}
