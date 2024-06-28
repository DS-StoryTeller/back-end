package com.cojac.storyteller.dto.profile;

import com.cojac.storyteller.domain.ProfileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private Integer id;
    private String name;
    private LocalDate birthDate;
    private String imageUrl;
    private String pinNumber;
    private Integer userId;

    public ProfileDTO mapEntityToDTO(ProfileEntity profileEntity) {
        return new ProfileDTO(
                profileEntity.getId(),
                profileEntity.getName(),
                profileEntity.getBirthDate(),
                profileEntity.getImageUrl(),
                profileEntity.getUser().getId()
        );
    }

    private ProfileDTO(Integer id, String name, LocalDate birthDate, String imageUrl, Integer userId) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }
}