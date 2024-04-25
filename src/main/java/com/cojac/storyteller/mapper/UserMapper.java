package com.cojac.storyteller.mapper;

import com.cojac.storyteller.domain.UserEntity;
import com.cojac.storyteller.dto.user.UserDTO;

public class UserMapper {

    public static UserEntity  mapToUserEntity(UserDTO userDTO, String encryptedPassword) {
        UserEntity user = UserEntity.builder()
                .password(encryptedPassword)
                .username(userDTO.getUsername())
                .role(userDTO.getRole())
                .build();

        return user;
    }

    public static UserDTO mapToUserDTO(UserEntity userEntity) {
        UserDTO userDTO = UserDTO.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .build();

        return userDTO;
    }
}
