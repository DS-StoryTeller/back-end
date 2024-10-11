package com.cojac.storyteller.common.swagger;

import com.cojac.storyteller.page.dto.PageDetailResponseDTO;
import com.cojac.storyteller.response.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Page Controller", description = "페이지 관련 API")
public interface PageControllerDocs {

    /**
     * 페이지 세부 정보 조회
     */
    @Operation(
            summary = "페이지 세부 정보 조회",
            description = "페이지 세부 정보 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "책 ID", required = true),
                    @Parameter(name = "pageNum", in = ParameterIn.PATH, description = "페이지 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "페이지 세부 정보를 성공적으로 조회했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "책을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "페이지를 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "단어를 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO<PageDetailResponseDTO>> getPageDetail(@PathVariable Integer profileId, @PathVariable Integer bookId, @PathVariable Integer pageNum);

}
