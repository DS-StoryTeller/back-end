package com.cojac.storyteller.controller.swagger;

import com.cojac.storyteller.dto.request.PageRequestDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "Page Controller", description = "페이지 관련 API")
public interface PageControllerDocs {

    /**
     * 페이지 세부 정보 조회
     */
    @Operation(
            summary = "페이지 세부 정보 조회",
            description = "페이지 세부 정보 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "페이지 세부 정보를 성공적으로 조회했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    ResponseEntity<ResponseDTO> getPageDetail(@ParameterObject @ModelAttribute PageRequestDTO pageRequestDTO);

    /**
     * 페이지 이미지 업데이트
     */
    @Operation(
            summary = "페이지 이미지 업데이트",
            description = "페이지 이미지 업데이트 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "페이지 이미지를 성공적으로 변경했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    ResponseEntity<ResponseDTO> updatePageDetail(@ParameterObject @ModelAttribute PageRequestDTO pageRequestDTO);

}
