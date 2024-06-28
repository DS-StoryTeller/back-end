package com.cojac.storyteller.service;

import com.cojac.storyteller.dto.profile.ProfileDTO;
import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
import com.cojac.storyteller.dto.user.UserDTO;
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

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setPassword("password");
        UserDTO savedUserDTO = userService.registerUser(userDTO);

        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setName("name");
        profileDTO.setBirthDate(LocalDate.of(2000, 1, 1));
        profileDTO.setImageUrl("https://example.com/profile.jpg");
        profileDTO.setUserId(savedUserDTO.getId());

        // When
        ProfileDTO createdProfile = profileService.createProfile(profileDTO);

        // Then
        assertNotNull(createdProfile.getId());
        assertEquals(profileDTO.getName(), createdProfile.getName());
        assertEquals(profileDTO.getBirthDate(), createdProfile.getBirthDate());
        assertEquals(profileDTO.getImageUrl(), createdProfile.getImageUrl());
        assertEquals(profileDTO.getUserId(), createdProfile.getUserId());
    }


}