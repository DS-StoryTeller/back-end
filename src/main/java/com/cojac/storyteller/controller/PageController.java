package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.page.PageDetailResponseDTO;
import com.cojac.storyteller.dto.request.PageRequestDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDTO;
import com.cojac.storyteller.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pages")
@RequiredArgsConstructor
@Tag(name = "Page Controller", description = "페이지 관련 API")
public class PageController {
    private final PageService pageService;

    /**
     * 페이지 세부 정보 조회
     */
    @GetMapping("/detail")
    @Operation(
            summary = "페이지 세부 정보 조회",
            description = "페이지 세부 정보 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "페이지 세부 정보를 성공적으로 조회했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO> getPageDetail(@ParameterObject @ModelAttribute PageRequestDTO pageRequestDTO) {
        PageDetailResponseDTO pageDetail = pageService.getPageDetail(pageRequestDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_RETRIEVE_PAGE_DETAILS.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_PAGE_DETAILS, pageDetail));
    }

    /**
     * 페이지 이미지 업데이트
     */
    @PostMapping(value = "/updateImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "페이지 세부 정보 조회",
            description = "페이지 세부 정보 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "페이지 이미지를 성공적으로 변경했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO> updatePageDetail(@RequestParam("imageFile") MultipartFile imageFile,
                                                        @ParameterObject @ModelAttribute PageRequestDTO pageRequestDTO) {
        PageDetailResponseDTO pageDetail = pageService.updatePageImage(pageRequestDTO, imageFile);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPDATE_PAGE_IMAGE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_PAGE_IMAGE, pageDetail));
    }
}
