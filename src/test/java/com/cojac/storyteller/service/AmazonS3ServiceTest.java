package com.cojac.storyteller.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AmazonS3ServiceTest {

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Test
    void getAllPhotos() {
        List<String> profilePhotos = amazonS3Service.getAllPhotos("profile/photos");
        assertEquals(4, profilePhotos.size());
    }
}