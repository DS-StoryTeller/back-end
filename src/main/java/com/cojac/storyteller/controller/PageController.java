package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.page.PageDetailResponseDTO;
import com.cojac.storyteller.dto.request.PageRequestDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pages")
@RequiredArgsConstructor
public class PageController {
    private final PageService pageService;

    @GetMapping("/detail")
    public ResponseDTO<PageDetailResponseDTO> getPageDetail(@ModelAttribute PageRequestDTO pageRequestDTO) {
        PageDetailResponseDTO pageDetail = pageService.getPageDetail(pageRequestDTO);
        return new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_PAGE_DETAILS, pageDetail);
    }
    @PostMapping("/updateImage")
    public ResponseDTO<PageDetailResponseDTO> updatePageDetail(
            @ModelAttribute PageRequestDTO pageRequestDTO) {
        PageDetailResponseDTO pageDetail = pageService.updatePageImage(pageRequestDTO);
        return new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_PAGE_IMAGE, pageDetail);
    }
}
