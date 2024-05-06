package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.UserEntity;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.exception.DuplicateUsernameException;
import com.cojac.storyteller.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDTO registerUser(UserDTO userDTO) {

        String username = userDTO.getUsername();
        String role = userDTO.getRole();
        // 패스워드를 암호화하여 저장
        String encryptedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());

        // 중복된 아이디가 이미 존재하는지 확인
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(ErrorCode.DUPLICATE_USERNAME);
        }

        // UserEntity 생성
        UserEntity userEntity = new UserEntity(encryptedPassword, username, role);

        userRepository.save(userEntity);

        return new UserDTO(userEntity.getId(), userEntity.getUsername(), userEntity.getRole());
    }

}
