package com.cojac.storyteller.service;

import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.profile.PinNumberDTO;
import com.cojac.storyteller.dto.profile.ProfileDTO;
import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
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

        ProfileDTO profileDTO = createProfileDTO(savedLocalUserDTO);

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

        LocalUserDTO savedLocalUserDTO = createUser();
        ProfileDTO profileDTO = createProfileDTO(savedLocalUserDTO);
        ProfileDTO createdProfile = profileService.createProfile(profileDTO);

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
        ProfileDTO profileDTO = createProfileDTO(savedLocalUserDTO);
        ProfileDTO createdProfile = profileService.createProfile(profileDTO);

        // 프로필 수정
        ProfileDTO updatedProfileDTO = new ProfileDTO();
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

    private static ProfileDTO createProfileDTO(LocalUserDTO savedLocalUserDTO) {
        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setName("name");
        profileDTO.setBirthDate(LocalDate.of(2000, 1, 1));
        profileDTO.setImageUrl("https://example.com/profile.jpg");
        profileDTO.setUserId(savedLocalUserDTO.getId());
        profileDTO.setPinNumber("1234");
        return profileDTO;
    }

    private LocalUserDTO createUser() {
        LocalUserDTO localUserDTO = new LocalUserDTO();
        localUserDTO.setUsername("username");
        localUserDTO.setPassword("password");
        LocalUserDTO savedLocalUserDTO = userService.registerUser(localUserDTO);
        return savedLocalUserDTO;
    }
}