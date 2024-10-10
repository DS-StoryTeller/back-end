package com.cojac.storyteller.common.swagger;

import com.cojac.storyteller.profile.dto.PinNumberDTO;
import com.cojac.storyteller.profile.dto.ProfileDTO;
import com.cojac.storyteller.response.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Profile Controller", description = "프로필 관련 API")
public interface ProfileControllerDocs {

    /**
     * 프로필 사진 목록 가져오기
     */
    @Operation(
            summary = "프로필 사진 목록 조회",
            description = "프로필 사진의 목록을 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 사진 목록을 성공적으로 조회했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> getProfilePhotos();

    /**
     * 프로필 생성하기
     */
    @Operation(
            summary = "프로필 생성",
            description = "새로운 프로필을 생성 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "프로필 생성 위한 정보",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{\"name\": \"프로필의 이름\", \"birthDate\": \"생년월일\", " +
                                                    "\"imageUrl\": \"프로필 이미지 URL\", \"pinNumber\": \"핀 번호\", " +
                                                    "\"userId\": \"사용자의 아이디\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필이 성공적으로 생성되었습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 핀 번호입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> createProfile(@Valid @RequestBody ProfileDTO profileDTO);

    /**
     * 프로필 비밀번호 검증하기
     */
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
                    @ApiResponse(responseCode = "200", description = "프로필의 비밀번호를 검증을 완료했습니다. valid를 확인해주세요.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> verificationPinNumber(@PathVariable Integer profileId,
                                                      @Valid @RequestBody PinNumberDTO pinNumberDTO);
    /**
     * 프로필 수정하기
     */
    @Operation(
            summary = "프로필 수정",
            description = "주어진 프로필 ID를 사용하여 프로필 정보를 수정 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "프로필 수정을 위한 정보",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{\"name\": \"프로필의 이름\", \"birthDate\": \"생년월일\", " +
                                                    "\"imageUrl\": \"프로필 이미지 URL\", \"pinNumber\": \"핀 번호\"}"
                                    )
                            }
                    )
            ),
            parameters = @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필이 성공적으로 수정되었습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필릏 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 핀 번호입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> updateProfile(@PathVariable Integer profileId,
                                              @Valid @RequestBody ProfileDTO profileDTO);

    /**
     * 프로필 정보 불러오기
     */
    @Operation(
            summary = "프로필 정보 불러오기",
            description = "주어진 프로필 ID를 사용하여 프로필 정보 조회 API",
            parameters = @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필을 성공적으로 조회했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필릏 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> getProfile(@PathVariable Integer profileId);

    /**
     * 프로필 목록 불러오기
     */
    @Operation(
            summary = "프로필 목록 불러오기",
            description = "주어진 조건으로 프로필 목록 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 목록을 성공적으로 조회했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> getProfileList(@PathVariable Integer userId);

    /**
     * 프로필 삭제하기
     */
    @Operation(
            summary = "프로필 삭제하기",
            description = "주어진 프로필 ID를 사용하여 프로필을 삭제 API",
            parameters = @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필이 성공적으로 삭제되었습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필릏 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> deleteProfile(@PathVariable Integer profileId) throws Exception;

    /**
     * 프로필 사진 S3에 업로드
     */
    @Hidden
    ResponseEntity<ResponseDTO> uploadProfilePhotos(@RequestParam("files") MultipartFile[] files) throws IOException;

}
