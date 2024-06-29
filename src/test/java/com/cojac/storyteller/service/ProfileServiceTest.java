package com.cojac.storyteller.service;

import com.cojac.storyteller.dto.profile.PinNumberDTO;
import com.cojac.storyteller.dto.profile.ProfileDTO;
import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.exception.InvalidPinNumberException;
import com.cojac.storyteller.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getProfilePhotos() {
        List<ProfilePhotoDTO> result = profileService.getProfilePhotos();
        assertEquals(4, result.size());
    }

    @Test
    void createProfile() {

        UserDTO savedUserDTO = createUser();

        ProfileDTO profileDTO = createProfileDTO(savedUserDTO);

        // When
        ProfileDTO createdProfile = profileService.createProfile(profileDTO);

        // Then
        assertNotNull(createdProfile.getId());
        assertEquals(profileDTO.getName(), createdProfile.getName());
        assertEquals(profileDTO.getBirthDate(), createdProfile.getBirthDate());
        assertEquals(profileDTO.getImageUrl(), createdProfile.getImageUrl());
        assertEquals(profileDTO.getUserId(), createdProfile.getUserId());
    }

    @Test
    void checkPinNum() {

        UserDTO savedUserDTO = createUser();
        ProfileDTO profileDTO = createProfileDTO(savedUserDTO);
        ProfileDTO createdProfile = profileService.createProfile(profileDTO);

        // 올바른 PIN으로 테스트
        PinNumberDTO validPinNumberDTO  = new PinNumberDTO();
        validPinNumberDTO.setProfileId(createdProfile.getId());
        validPinNumberDTO.setPinNumber("1234");
        assertDoesNotThrow(() -> profileService.checkPinNumber(validPinNumberDTO));

        // 잘못된 PIN으로 테스트
        PinNumberDTO invalidPinNumberDTO = new PinNumberDTO();
        invalidPinNumberDTO.setProfileId(createdProfile.getId());
        invalidPinNumberDTO.setPinNumber("4321");
        assertThrows(InvalidPinNumberException.class, () -> profileService.checkPinNumber(invalidPinNumberDTO));
    }

    private static ProfileDTO createProfileDTO(UserDTO savedUserDTO) {
        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setName("name");
        profileDTO.setBirthDate(LocalDate.of(2000, 1, 1));
        profileDTO.setImageUrl("https://example.com/profile.jpg");
        profileDTO.setUserId(savedUserDTO.getId());
        profileDTO.setPinNumber("1234");
        return profileDTO;
    }

    private UserDTO createUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setPassword("password");
        UserDTO savedUserDTO = userService.registerUser(userDTO);
        return savedUserDTO;
    }
}