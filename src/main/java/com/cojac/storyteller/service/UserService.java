package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.LocalUserEntity;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.dto.user.UsernameDTO;
import com.cojac.storyteller.exception.DuplicateUsernameException;
import com.cojac.storyteller.exception.UsernameExistsException;
import com.cojac.storyteller.repository.LocalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final LocalUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 자체 회원가입하기
     * @param userDTO 사용자 DTO
     * @return 등록된 사용자 DTO
     */
    @Transactional
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
        LocalUserEntity localUserEntity = new LocalUserEntity(encryptedPassword, username, role);

        userRepository.save(localUserEntity);

        return new UserDTO(localUserEntity.getId(), localUserEntity.getUsername(), localUserEntity.getRole());
    }

    /**
     * 아이디 중복 확인하기
     */
    public UsernameDTO checkUsername(UsernameDTO usernameDTO) {

        String username = usernameDTO.getUsername();
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new UsernameExistsException(ErrorCode.DUPLICATE_USERNAME);
                });

        return new UsernameDTO(username);
    }
}
