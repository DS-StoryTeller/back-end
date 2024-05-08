package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDetailDto;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDto;
import com.cojac.storyteller.service.UnknownWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unknownwords")
@RequiredArgsConstructor
public class UnknownWordController {
    private final UnknownWordService unknownWordService;
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<UnknownWordDetailDto>> createUnknownWord(
            @RequestParam Integer profileId,
            @RequestParam Integer bookId,
            @RequestParam Integer pageNum,
            @RequestBody UnknownWordDto createUnknownWordDto){
        UnknownWordDetailDto response = unknownWordService.saveUnknownWord(profileId, bookId, pageNum, createUnknownWordDto);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_UNKNOWNWORD, response));
    }
}
