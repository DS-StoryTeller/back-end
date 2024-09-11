package com.cojac.storyteller.docs;

import com.cojac.storyteller.dto.request.PageRequestDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "UnknownWord Controller", description = "단어 관련 API")
public interface UnknownWordControllerDocs {

    /**
     * 모르는 단어 저장하기
     */
    @Operation(
            summary = "단어 저장",
            description = "단어 저장 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "단어 저장을 위한 정보",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{\"unknownWord\": \"모르는 단어\", \"position\": \"위치\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "단어가 성공적으로 저장되었습니다", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "책을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "페이지를 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO<UnknownWordDetailDTO>> createUnknownWord(@ParameterObject @ModelAttribute PageRequestDTO pageRequestDTO,
                                                                        @RequestBody UnknownWordDTO unknownWordDto);

    /**
     * 모르는 단어 삭제하기
     */
    @Operation(
            summary = "단어 삭제",
            description = "단어 삭제 API",
            parameters = {
                    @Parameter(name = "unknownWordId", in = ParameterIn.PATH, description = "단어 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "단어가 성공적으로 삭제되었습니다", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> deleteUnknownWord(@PathVariable("unknownWordId") Integer unknownWordId);

}
