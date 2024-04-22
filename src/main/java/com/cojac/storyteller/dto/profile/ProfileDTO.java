package com.cojac.storyteller.dto.profile;

import com.cojac.storyteller.dto.book.BookDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private Integer id;
    private String name;
    private Integer age;
    private String image;
    private Integer userId;
    private List<BookDTO> books;
}