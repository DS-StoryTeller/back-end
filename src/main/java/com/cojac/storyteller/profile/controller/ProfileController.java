package com.cojac.storyteller.profile.controller;

import com.cojac.storyteller.profile.service.ProfileService;
import com.cojac.storyteller.response.code.ResponseCode;
import com.cojac.storyteller.common.swagger.ProfileControllerDocs;
import com.cojac.storyteller.response.dto.ResponseDTO;
import com.cojac.storyteller.profile.dto.PinCheckResultDTO;
import com.cojac.storyteller.profile.dto.PinNumberDTO;
import com.cojac.storyteller.profile.dto.ProfileDTO;
import com.cojac.storyteller.profile.dto.ProfilePhotoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfileController implements ProfileControllerDocs {

    private final ProfileService profileService;

    /**
     * 프로필 사진 목록 가져오기
     */
    @GetMapping("/profiles/photos")
    public ResponseEntity<ResponseDTO> getProfilePhotos() {
        List<ProfilePhotoDTO> result = profileService.getProfilePhotos();
        return ResponseEntity
                .status(ResponseCode.SUCCESS_PROFILE_PHOTOS.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_PROFILE_PHOTOS, result));
    }

    /**
     * 프로필 생성하기
     */
    @PostMapping("/profiles")
    public ResponseEntity<ResponseDTO> createProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        ProfileDTO result = profileService.createProfile(profileDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_PROFILE, result));
    }

    /**
     * 프로필 비밀번호 검증하기
     */
    @PostMapping("/profiles/{profileId}/pin-number/verifications")
    public ResponseEntity<ResponseDTO> verificationPinNumber(@PathVariable Integer profileId,
                                                             @Valid @RequestBody PinNumberDTO pinNumberDTO) {
        PinCheckResultDTO res = profileService.verificationPinNumber(profileId, pinNumberDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_VERIFICATION_PIN_NUMBER.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_VERIFICATION_PIN_NUMBER, res));
    }

    /**
     * 프로필 수정하기
     */
    @PutMapping("/profiles/{profileId}")
    public ResponseEntity<ResponseDTO> updateProfile(@PathVariable Integer profileId,
                                                     @Valid @RequestBody ProfileDTO profileDTO) {
        ProfileDTO result = profileService.updateProfile(profileId, profileDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPDATE_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_PROFILE, result));
    }

    /**
     * 프로필 정보 불러오기
     */
    @GetMapping("/profiles/{profileId}")
    public ResponseEntity<ResponseDTO> getProfile(@PathVariable Integer profileId) {
        ProfileDTO result = profileService.getProfile(profileId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_GET_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_GET_PROFILE, result));
    }

    /**
     * 프로필 목록 불러오기
     */
    @GetMapping("/users/{userId}/profiles")
    public ResponseEntity<ResponseDTO> getProfileList(@PathVariable Integer userId) {
        List<ProfileDTO> result = profileService.getProfileList(userId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_GET_PROFILE_LIST.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_GET_PROFILE_LIST, result));
    }

    /**
     * 프로필 삭제하기
     */
    @DeleteMapping("/profiles/{profileId}")
    public ResponseEntity<ResponseDTO> deleteProfile(@PathVariable Integer profileId) throws Exception {
        profileService.deleteProfile(profileId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_DELETE_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_PROFILE, null));
    }

    /**
     * 프로필 사진 S3에 업로드
     */
    @PostMapping(value = "/profiles/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> uploadProfilePhotos(@RequestParam("files") MultipartFile[] files) throws IOException {
        profileService.uploadMultipleFilesToS3(files);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPLOAD_PHOTOS.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPLOAD_PHOTOS, null));
    }
}
