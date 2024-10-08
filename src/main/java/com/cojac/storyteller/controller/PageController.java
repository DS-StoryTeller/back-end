package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.docs.PageControllerDocs;
import com.cojac.storyteller.dto.page.PageDetailResponseDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.PageService;
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
