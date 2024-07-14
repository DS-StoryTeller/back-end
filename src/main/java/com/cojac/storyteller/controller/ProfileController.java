package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.profile.PinCheckResultDTO;
import com.cojac.storyteller.dto.profile.PinNumberDTO;
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
    @GetMapping("/photos")
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

    /**
     * 프로필 비밀번호 체크하기
     */
    @PostMapping("/{profileId}/pin-number/verifications")
    public ResponseEntity<ResponseDTO> verificationPinNumber(@PathVariable Integer profileId,
                                                      @RequestBody PinNumberDTO pinNumberDTO) {
        PinCheckResultDTO res = profileService.verificationPinNumber(profileId, pinNumberDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_VERIFICATION_PIN_NUMBER.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_VERIFICATION_PIN_NUMBER, res));
    }

    /**
     * 프로필 수정하기
     */
    @PutMapping("/{profileId}")
    public ResponseEntity<ResponseDTO> updateProfile(@PathVariable Integer profileId,
                                                     @RequestBody ProfileDTO profileDTO) {
        ProfileDTO result = profileService.updateProfile(profileId, profileDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPDATE_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_PROFILE, result));
    }

    /**
     * 프로필 정보 불러오기
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<ResponseDTO> getProfile(@PathVariable Integer profileId) {
        ProfileDTO result = profileService.getProfile(profileId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_GET_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_GET_PROFILE, result));
    }

    /**
     * 프로필 목록 불러오기
     */
    @GetMapping
    public ResponseEntity<ResponseDTO> getProfileList(@RequestBody ProfileDTO profileDTO) {
        List<ProfileDTO> result = profileService.getProfileList(profileDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_GET_PROFILE_LIST.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_GET_PROFILE_LIST, result));
    }

    /**
     * 프로필 삭제하기
     */
    @DeleteMapping("/{profileId}")
    public ResponseEntity<ResponseDTO> deleteProfile(@PathVariable Integer profileId) {
        profileService.deleteProfile(profileId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_DELETE_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_PROFILE, null));
    }


}
