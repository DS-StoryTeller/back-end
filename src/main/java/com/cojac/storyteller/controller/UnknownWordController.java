package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.controller.swagger.UnknownWordControllerDocs;
import com.cojac.storyteller.dto.request.PageRequestDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDetailDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDTO;
import com.cojac.storyteller.service.UnknownWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<UnknownWordDetailDTO>> createUnknownWord(@ParameterObject  @ModelAttribute PageRequestDTO pageRequestDTO,
                                                                               @RequestBody UnknownWordDTO unknownWordDto) {
        UnknownWordDetailDTO response = unknownWordService.saveUnknownWord(pageRequestDTO, unknownWordDto);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_UNKNOWNWORD.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_UNKNOWNWORD, response));
    }

    /**
     * 모르는 단어 삭제하기
     */
    @DeleteMapping("/delete/{unknownWordId}")
    @Operation(
            summary = "단어 삭제",
            description = "단어 삭제 API",
            parameters = {
                    @Parameter(name = "unknownWordId", in = ParameterIn.PATH, description = "단어 ID", required = true)
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
