package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.profile.*;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile Controller", description = "프로필 관련 API")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 프로필 사진 목록 가져오기
     */
    @GetMapping("/photos")
    @Operation(
            summary = "프로필 사진 목록 조회",
            description = "프로필 사진의 목록을 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 사진 목록을 성공적으로 조회했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
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
    @Operation(
            summary = "프로필 생성",
            description = "새로운 프로필을 생성 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "프로필 생성을 위한 정보",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CreateProfileDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필이 성공적으로 생성되었습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO> createProfile(@Valid @RequestBody CreateProfileDTO createProfileDTO) {
        ProfileDTO result = profileService.createProfile(createProfileDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_PROFILE, result));
    }

    /**
     * 프로필 비밀번호 검증하기
     */
    @PostMapping("/{profileId}/pin-number/verifications")
    @Operation(
            summary = "프로필 비밀번호 검증",
            description = "주어진 프로필 ID와 비밀번호를 검증 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비밀번호 검증 정보",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PinNumberDTO.class)
                    )
            ),
            parameters = @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필의 비밀번호를 검증을 완료했습니다. valid를 확인해주세요.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
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
    @PutMapping("/{profileId}")
    @Operation(
            summary = "프로필 수정",
            description = "주어진 프로필 ID를 사용하여 프로필 정보를 수정 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 프로필 정보",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileDTO.class)
                    )
            ),
            parameters = @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필이 성공적으로 수정되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDTO.class)))
            }
    )
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
    @GetMapping("/{profileId}")
    @Operation(
            summary = "프로필 정보 불러오기",
            description = "주어진 프로필 ID를 사용하여 프로필 정보 조회 API",
            parameters = @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필을 성공적으로 조회했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDTO.class)))
            }
    )
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
    @Operation(
            summary = "프로필 목록 불러오기",
            description = "주어진 조건으로 프로필 목록 조회 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "검색 조건에 사용할 프로필 정보",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 목록을 성공적으로 조회했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDTO.class)))
            }
    )
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
    @Operation(
            summary = "프로필 삭제하기",
            description = "주어진 프로필 ID를 사용하여 프로필을 삭제 API",
            parameters = @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필이 성공적으로 삭제되었습니다."),
            }
    )
    public ResponseEntity<ResponseDTO> deleteProfile(@PathVariable Integer profileId) {
        profileService.deleteProfile(profileId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_DELETE_PROFILE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_PROFILE, null));
    }

    /**
     * 프로필 사진 S3에 업로드
     */
    @PostMapping(value = "/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "프로필 사진 업로드",
            description = "프로필 사진을 S3에 업로드합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 사진들을 성공적으로 업로드했습니다."),
            }
    )
    public ResponseEntity<ResponseDTO> uploadProfilePhotos(@RequestParam("files") MultipartFile[] files) throws IOException {
        profileService.uploadMultipleFilesToS3(files);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPLOAD_PHOTOS.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPLOAD_PHOTOS, null));
    }
}
