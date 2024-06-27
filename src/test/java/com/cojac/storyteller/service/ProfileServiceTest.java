package com.cojac.storyteller.service;

import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;

    @Test
    void getProfilePhotos() {
        List<ProfilePhotoDTO> result = profileService.getProfilePhotos();
        assertEquals(4, result.size());
    }
}