package com.cojac.storyteller.service;

import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.profile.*;
import com.cojac.storyteller.dto.user.LocalUserDTO;
import com.cojac.storyteller.exception.InvalidPinNumberException;
import com.cojac.storyteller.repository.ProfileRepository;
import com.cojac.storyteller.repository.LocalUserRepository;
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
    private LocalUserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void getProfilePhotos() {
        List<ProfilePhotoDTO> result = profileService.getProfilePhotos();
        assertEquals(4, result.size());
    }

    @Test
    void createProfile() {

        LocalUserDTO savedLocalUserDTO = createUser();

        CreateProfileDTO createProfileDTO = createProfileDTO(savedLocalUserDTO);

        // When
        ProfileDTO createdProfile = profileService.createProfile(createProfileDTO);

        // Then
        assertNotNull(createdProfile.getId());
        assertEquals(createProfileDTO.getName(), createdProfile.getName());
        assertEquals(createProfileDTO.getBirthDate(), createdProfile.getBirthDate());
        assertEquals(createProfileDTO.getImageUrl(), createdProfile.getImageUrl());
        assertEquals(createProfileDTO.getUserId(), createdProfile.getUserId());
    }

    @Test
    void checkPinNum() {

        LocalUserDTO savedLocalUserDTO = createUser();
        CreateProfileDTO createProfileDTO = createProfileDTO(savedLocalUserDTO);
        ProfileDTO createdProfile = profileService.createProfile(createProfileDTO);

        // 올바른 PIN으로 테스트
        PinNumberDTO validPinNumberDTO  = new PinNumberDTO();
        validPinNumberDTO.setPinNumber("1234");
        assertDoesNotThrow(() -> profileService.verificationPinNumber(createdProfile.getId(), validPinNumberDTO));

        // 잘못된 PIN으로 테스트
        PinNumberDTO invalidPinNumberDTO = new PinNumberDTO();
        invalidPinNumberDTO.setPinNumber("4321");
        assertThrows(InvalidPinNumberException.class, () -> profileService.verificationPinNumber(createdProfile.getId(), invalidPinNumberDTO));
    }

    @Test
    void updateProfile() {

        LocalUserDTO savedLocalUserDTO = createUser();
        CreateProfileDTO createProfileDTO = createProfileDTO(savedLocalUserDTO);
        ProfileDTO createdProfile = profileService.createProfile(createProfileDTO);

        // 프로필 수정
        UpdateProfileDTO updatedProfileDTO = new UpdateProfileDTO();
        updatedProfileDTO.setName("updatedName");
        updatedProfileDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        updatedProfileDTO.setImageUrl("https://example.com/updated_profile.jpg");
        updatedProfileDTO.setPinNumber("4321");

        profileService.updateProfile(createdProfile.getId(), updatedProfileDTO);

        ProfileEntity updatedProfile = profileRepository.findById(createdProfile.getId()).get();

        // 수정된 프로필 검증
        assertNotNull(updatedProfile.getId());
        assertEquals(updatedProfileDTO.getName(), updatedProfile.getName());
        assertEquals(updatedProfileDTO.getBirthDate(), updatedProfile.getBirthDate());
        assertEquals(updatedProfileDTO.getImageUrl(), updatedProfile.getImageUrl());
        assertEquals(updatedProfileDTO.getPinNumber(), updatedProfile.getPinNumber());
    }

    private static CreateProfileDTO createProfileDTO(LocalUserDTO savedLocalUserDTO) {
        CreateProfileDTO createProfileDTO = new CreateProfileDTO();

        createProfileDTO.setName("name");
        createProfileDTO.setBirthDate(LocalDate.of(2000, 1, 1));
        createProfileDTO.setImageUrl("https://example.com/profile.jpg");
        createProfileDTO.setUserId(savedLocalUserDTO.getId());
        createProfileDTO.setPinNumber("1234");
        return createProfileDTO;
    }

    private LocalUserDTO createUser() {
        LocalUserDTO localUserDTO = new LocalUserDTO();
        localUserDTO.setUsername("username");
        localUserDTO.setPassword("password");
        LocalUserDTO savedLocalUserDTO = userService.registerUser(localUserDTO);
        return savedLocalUserDTO;
    }
}