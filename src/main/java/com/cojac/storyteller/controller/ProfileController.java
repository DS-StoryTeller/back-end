package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.profile.ProfileDTO;
import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 프로필 사진 목록 가져오기
     */
    @GetMapping
    public ResponseEntity<ResponseDTO> getProfilePhotos() {
        List<ProfilePhotoDTO> result = profileService.getProfilePhotos();
        return ResponseEntity
                .status(ResponseCode.SUCCESS_PROFILE_PHOTOS.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_PROFILE_PHOTOS, result));
    }

    /**
     * 프로필 생성하기
     */
    @PostMapping
    public ResponseEntity<ResponseDTO> createProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO result = profileService.createProfile(profileDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_PROFILE, result));
    }



}
