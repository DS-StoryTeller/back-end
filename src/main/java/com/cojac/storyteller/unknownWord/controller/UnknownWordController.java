package com.cojac.storyteller.unknownWord.controller;

import com.cojac.storyteller.response.code.ResponseCode;
import com.cojac.storyteller.common.swagger.UnknownWordControllerDocs;
import com.cojac.storyteller.response.dto.ResponseDTO;
import com.cojac.storyteller.unknownWord.dto.UnknownWordDetailDTO;
import com.cojac.storyteller.unknownWord.dto.UnknownWordRequestDTO;
import com.cojac.storyteller.unknownWord.service.UnknownWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unknownwords")
@RequiredArgsConstructor
public class UnknownWordController implements UnknownWordControllerDocs {

    private final UnknownWordService unknownWordService;

    /**
     * 모르는 단어 저장하기
     */
    @PostMapping
    public ResponseEntity<ResponseDTO<UnknownWordDetailDTO>> createUnknownWord(@RequestBody UnknownWordRequestDTO unknownWordRequestDTO) {
        UnknownWordDetailDTO response = unknownWordService.saveUnknownWord(unknownWordRequestDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_UNKNOWNWORD.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_UNKNOWNWORD, response));
    }

    /**
     * 모르는 단어 삭제하기
     */
    @DeleteMapping("/{unknownWordId}")
    @Operation(
            summary = "단어 삭제",
            description = "단어 삭제 API",
            parameters = {
                    @Parameter(name = "unknownWordId", in = ParameterIn.PATH, description = "모르는 단어 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "단어가 성공적으로 삭제되었습니다", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO> deleteUnknownWord(@PathVariable("unknownWordId") Integer unknownWordId) {
        unknownWordService.deleteUnknownWord(unknownWordId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_DELETE_UNKNOWNWORD.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_UNKNOWNWORD, null));
    }
}
