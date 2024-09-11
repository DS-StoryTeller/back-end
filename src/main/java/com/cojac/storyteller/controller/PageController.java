package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.controller.swagger.PageControllerDocs;
import com.cojac.storyteller.dto.page.PageDetailResponseDTO;
import com.cojac.storyteller.dto.request.PageRequestDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pages")
@RequiredArgsConstructor
public class PageController implements PageControllerDocs {
    private final PageService pageService;

    /**
     * 페이지 세부 정보 조회
     */
    @GetMapping("/detail")
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
    public ResponseEntity<ResponseDTO> updatePageDetail(@ParameterObject @ModelAttribute PageRequestDTO pageRequestDTO) {
        PageDetailResponseDTO pageDetail = pageService.updatePageImage(pageRequestDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPDATE_PAGE_IMAGE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_PAGE_IMAGE, pageDetail));
    }
}
