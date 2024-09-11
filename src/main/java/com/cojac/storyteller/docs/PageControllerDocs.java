package com.cojac.storyteller.docs;

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
                    @ApiResponse(responseCode = "200", description = "페이지 세부 정보를 성공적으로 조회했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "404", description = "책을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "404", description = "페이지를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "404", description = "단어를 찾을 수 없습니다."),
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
                    @ApiResponse(responseCode = "200", description = "페이지 이미지를 성공적으로 변경했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "404", description = "책을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "404", description = "페이지를 찾을 수 없습니다."),
            }
    )
    ResponseEntity<ResponseDTO> updatePageDetail(@ParameterObject @ModelAttribute PageRequestDTO pageRequestDTO);

}
