package com.cojac.storyteller.setting.controller;

import com.cojac.storyteller.response.code.ResponseCode;
import com.cojac.storyteller.common.swagger.SettingControllerDocs;
import com.cojac.storyteller.response.dto.ResponseDTO;
import com.cojac.storyteller.setting.dto.SettingDTO;
import com.cojac.storyteller.setting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles/{profileId}/books/{bookId}/settings")
@RequiredArgsConstructor
public class SettingController implements SettingControllerDocs {
    private final SettingService settingService;

    /**
     * 책 설정 업데이트
     */
    @PutMapping
    public ResponseEntity<ResponseDTO<SettingDTO>> updateSettings(
            @PathVariable Integer profileId,
            @PathVariable Integer bookId,
            @RequestBody SettingDTO settingDTO) {
        SettingDTO response = settingService.updateSetting(profileId, bookId, settingDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPDATE_SETTING.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_SETTING, response));
    }

    /**
     * 책 설정 조회하기
     */
    @GetMapping
    public ResponseEntity<ResponseDTO<SettingDTO>> getDetailSettings(
            @PathVariable Integer profileId,
            @PathVariable Integer bookId) {
        SettingDTO response = settingService.getDetailSettings(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_RETRIEVE_SETTING.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_SETTING, response));
    }
}
