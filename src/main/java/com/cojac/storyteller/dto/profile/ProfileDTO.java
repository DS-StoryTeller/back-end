package com.cojac.storyteller.dto.profile;

import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.book.BookDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private Integer id;
    private String name;
    private LocalDate birthDate;
    private String imageUrl;
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
}