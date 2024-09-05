package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.setting.SettingDTO;
import com.cojac.storyteller.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Tag(name = "Setting Controller", description = "설정 관련 API")
public class SettingController {
    private final SettingService settingService;

    /**
     * 책 설정 업데이트
     */
    @PutMapping("/update")
    @Operation(
            summary = "책 설정 업데이트",
            description = "책 설정 업데이트 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.QUERY, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.QUERY, description = "책 ID", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업데이트할 책 설정 정보",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SettingDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "책 설정을 성공적으로 변경했습니다", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<SettingDTO>> updateSettings(
            @RequestParam Integer profileId,
            @RequestParam Integer bookId,
            @RequestBody SettingDTO settingDTO) {
        SettingDTO response = settingService.updateSetting(profileId, bookId, settingDTO);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_SETTING, response));
    }

    /**
     * 책 설정 조회하기
     */
    @GetMapping("/detail")
    @Operation(
            summary = "책 설정 조회",
            description = "책 설정 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.QUERY, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.QUERY, description = "책 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "책 설정을 성공적으로 조회했습니다", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    ResponseEntity<ResponseDTO<SettingDTO>> getDetailSettings(
            @RequestParam Integer profileId,
            @RequestParam Integer bookId) {
        SettingDTO response = settingService.getDetailSettings(profileId, bookId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_SETTING, response));
    }
}
