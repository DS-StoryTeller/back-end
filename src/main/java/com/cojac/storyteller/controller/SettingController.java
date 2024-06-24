package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.setting.SettingDTO;
import com.cojac.storyteller.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {
    private final SettingService settingService;

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO<SettingDTO>> updateSettings(
            @RequestParam Integer profileId,
            @RequestParam Integer bookId,
            @RequestBody SettingDTO settingDTO) {
        SettingDTO response = settingService.updateSetting(profileId, bookId, settingDTO);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_SETTING, response));
    }

    @GetMapping("/detail")
    ResponseEntity<ResponseDTO<SettingDTO>> getDetailSettings(
            @RequestParam Integer profileId,
            @RequestParam Integer bookId) {
        SettingDTO response = settingService.getDetailSettings(profileId, bookId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_SETTING, response));
    }
}
