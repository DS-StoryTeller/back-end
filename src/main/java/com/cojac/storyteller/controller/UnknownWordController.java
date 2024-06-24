package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.request.PageRequestDTO;
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
            @ModelAttribute  PageRequestDTO pageRequestDTO,
            @RequestBody UnknownWordDto unknownWordDto){
        UnknownWordDetailDto response = unknownWordService.saveUnknownWord(pageRequestDTO, unknownWordDto);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_UNKNOWNWORD, response));
    }

    @DeleteMapping("/delete/{unknownWordId}")
    public ResponseEntity<ResponseDTO> deleteUnknownWord(
            @PathVariable("unknownWordId") Integer unknownWordId
    ){
        unknownWordService.deleteUnknownWord(unknownWordId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_UNKNOWNWORD, null));
    }
}
