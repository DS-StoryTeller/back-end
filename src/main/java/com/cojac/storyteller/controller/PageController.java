package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.page.PageDetailResponseDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pages")
@RequiredArgsConstructor
public class PageController {
    private final PageService pageService;

    @GetMapping("/detail")
    public ResponseDTO<PageDetailResponseDTO> getPageDetail(
            @RequestParam Integer profileId,
            @RequestParam Integer bookId,
            @RequestParam Integer pageNum ) {
        PageDetailResponseDTO pageDetail = pageService.getPageDetail(profileId, bookId, pageNum);
        return new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_PAGE_DETAILS, pageDetail);
    }
}
