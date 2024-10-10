package com.cojac.storyteller.common.swagger;

import com.cojac.storyteller.book.dto.BookDTO;
import com.cojac.storyteller.book.dto.BookDetailResponseDTO;
import com.cojac.storyteller.book.dto.BookListResponseDTO;
import com.cojac.storyteller.book.dto.CreateBookRequest;
import com.cojac.storyteller.response.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Book Controller", description = "동화 관련 API")
public interface BookControllerDocs {

    /**
     * 동화 내용 생성
     */
    @Operation(
            summary = "동화 내용 생성",
            description = "동화 내용 생성 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "동화 생성에 필요한 정보",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CreateBookRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "동화가 성공적으로 생성되었습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),

            }
    )
    ResponseEntity<ResponseDTO> createBook(@PathVariable Integer profileId, @RequestBody CreateBookRequest request);

    /**
     * 동화 목록 조회
     */
    @Operation(
            summary = "동화 목록 조회",
            description = "사용자의 모든 동화 목록을 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "동화 목록을 성공적으로 조회했습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO>  getBookList(
            @PathVariable Integer profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size);

    /**
     * 동화 세부 정보 조회
     */
    @Operation(
            summary = "동화 세부 정보 조회",
            description = "동화 세부 정보를 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "동화 세부 정보를 성공적으로 조회했습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "책을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO<BookDetailResponseDTO>> getBookDetail(@PathVariable Integer profileId, @PathVariable Integer bookId);

    /**
     * 즐겨찾기 상태로 업데이트
     */
    @Operation(
            summary = "즐겨찾기 상태로 업데이트",
            description = "동화 즐겨찾기 상태를 업데이트 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "즐겨찾기 상태를 성공적으로 변경했습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "책을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO<Boolean>> isFavorite(@PathVariable Integer profileId, @PathVariable Integer bookId);

    /**
     * 동화 삭제
     */
    @Operation(
            summary = "동화 삭제",
            description = "동화 삭제 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "동화를 성공적으로 삭제했습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "책을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> deleteBook(@PathVariable Integer profileId, @PathVariable Integer bookId) throws Exception;

    /**
     * 현재 읽고 있는 페이지 업데이트
     */
    @Operation(
            summary = "현재 읽고 있는 페이지 업데이트",
            description = "동화 현재 페이지를 업데이트 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true),
                    @Parameter(name = "currentPage", in = ParameterIn.PATH, description = "현재 읽고 있는 페이지", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "현재 읽고 있는 페이지를 성공적으로 변경했습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "책을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO<BookDTO>> updateCurrentPage(@PathVariable Integer profileId, @PathVariable Integer bookId, @RequestParam Integer currentPage);

    /**
     * 즐겨찾기 동화 필터링
     */
    @Operation(
            summary = "즐겨찾기 동화 조회",
            description = "즐겨찾기 목록에 있는 동화 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "즐겨찾기 목록을 성공적으로 조회했습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getFavoriteBooks(
            @PathVariable Integer profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort);

    /**
     * 읽고 있는 동화 필터링
     */
    @Operation(
            summary = "읽고 있는 동화 필터링",
            description = "읽고 있는 동화 목록을 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "읽고 있는 동화 목록을 성공적으로 조회했습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getReadingBooks(
            @PathVariable Integer profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort);

    /**
     * 동화 퀴즈 생성
     */
    @Operation(
            summary = "동화 퀴즈 생성",
            description = "동화 퀴즈를 생성 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "퀴즈가 성공적으로 생성되었습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "책을 찾을 수 없습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            }
    )
    ResponseEntity<ResponseDTO> createQuiz(@PathVariable Integer profileId, @PathVariable Integer bookId);

}
