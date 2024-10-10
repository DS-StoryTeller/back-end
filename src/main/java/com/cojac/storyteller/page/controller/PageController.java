package com.cojac.storyteller.page.controller;

import com.cojac.storyteller.page.service.PageService;
import com.cojac.storyteller.response.code.ResponseCode;
import com.cojac.storyteller.common.swagger.PageControllerDocs;
import com.cojac.storyteller.page.dto.PageDetailResponseDTO;
import com.cojac.storyteller.response.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PageController implements PageControllerDocs {
    private final PageService pageService;

    @GetMapping("/profiles/{profileId}/books/{bookId}/pages/{pageNum}")
    public ResponseEntity<ResponseDTO<PageDetailResponseDTO>> getPageDetail(
            @PathVariable Integer profileId,
            @PathVariable Integer bookId,
            @PathVariable Integer pageNum) {
        PageDetailResponseDTO pageDetail = pageService.getPageDetail(profileId, bookId, pageNum);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_RETRIEVE_PAGE_DETAILS.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_PAGE_DETAILS, pageDetail));
    }

}
