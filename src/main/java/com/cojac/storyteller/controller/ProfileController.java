package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/profile")
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

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createProfile(
            @RequestPart(value = "name") String name,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        profileService.createProfile(name, file);
        return new ResponseEntity(null, HttpStatus.OK);
    }


}
