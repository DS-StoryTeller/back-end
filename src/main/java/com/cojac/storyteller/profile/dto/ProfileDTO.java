package com.cojac.storyteller.profile.dto;

import com.cojac.storyteller.profile.entity.ProfileEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private Integer id;
    @NotBlank(message = "name을 입력해주세요.")
    private String name;
    @NotNull(message = "birthDate를 입력해주세요.")
    private LocalDate birthDate;
    @NotBlank(message = "imageUrl를 입력해주세요.")
    private String imageUrl;
    @NotBlank(message = "pinNumber를 입력해주세요.")
    private String pinNumber;
    private Integer userId;

    public static ProfileDTO mapEntityToDTO(ProfileEntity profileEntity) {
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