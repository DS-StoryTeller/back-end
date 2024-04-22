package com.cojac.storyteller.dto.profile;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileDTO {

    private Integer id;
    private String name;
    private Integer age;
    private String image;
    private Integer userId;

    public ProfileDTO(Integer id, String name, Integer age, String image, Integer userId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.image = image;
        this.userId = userId;
    }
}
