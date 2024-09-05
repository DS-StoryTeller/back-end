package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.request.PageRequestDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDetailDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDTO;
import com.cojac.storyteller.service.UnknownWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unknownwords")
@RequiredArgsConstructor
@Tag(name = "UnknownWord Controller", description = "단어 관련 API")
public class UnknownWordController {

    private final UnknownWordService unknownWordService;

    /**
     * 모르는 단어 저장하기
     */
    @PostMapping("/create")
    @Operation(
            summary = "단어 저장",
            description = "단어 저장 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "단어 저장에 필요한 정보",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UnknownWordDTO.class)
                    )
            ),
            parameters = {
                    @Parameter(name = "profileId", description = "프로필 ID", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "bookId", description = "책 ID", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "pageNum", description = "페이지 번호", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "단어가 성공적으로 저장되었습니다", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<UnknownWordDetailDTO>> createUnknownWord(
            @ModelAttribute PageRequestDTO pageRequestDTO,
            @RequestBody UnknownWordDTO unknownWordDto) {
        UnknownWordDetailDTO response = unknownWordService.saveUnknownWord(pageRequestDTO, unknownWordDto);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_UNKNOWNWORD, response));
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
    public ResponseEntity<ResponseDTO> deleteUnknownWord(
            @PathVariable("unknownWordId") Integer unknownWordId
    ) {
        unknownWordService.deleteUnknownWord(unknownWordId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_UNKNOWNWORD, null));
    }
}
