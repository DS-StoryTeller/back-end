package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.book.*;
import com.cojac.storyteller.dto.response.ErrorResponseDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDTO;
import com.cojac.storyteller.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book Controller", description = "동화 관련 API")
public class BookController {

    private final BookService bookService;

    /**
     * 동화 내용 생성
     */
    @PostMapping("/create")
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
                    @ApiResponse(responseCode = "200", description = "동화가 성공적으로 생성되었습니다.", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<?>> createBook(@RequestBody CreateBookRequest request, @RequestParam Integer profileId) {
        BookDTO  createdBook = bookService.createBook(request.getPrompt(), profileId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_BOOK, createdBook));
    }

    /**
     * 동화 목록 조회
     */
    @GetMapping("/booklist")
    @Operation(
            summary = "동화 목록 조회",
            description = "사용자의 모든 동화 목록을 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "동화 목록을 성공적으로 조회했습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getBookList(@RequestParam Integer profileId) {
        List<BookListResponseDTO> books = bookService.getAllBooks(profileId);
        if (books.isEmpty()) {
            return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST, books));
        }
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_BOOKS, books));
    }

    /**
     * 동화 세부 정보 조회
     */
    @GetMapping("/detail")
    @Operation(
            summary = "동화 세부 정보 조회",
            description = "동화 세부 정보를 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "동화 세부 정보를 성공적으로 조회했습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<BookDetailResponseDTO>> getBookDetail(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        BookDetailResponseDTO bookDetail = bookService.getBookDetail(profileId, bookId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_BOOK_DETAILS, bookDetail));
    }

    /**
     * 즐겨찾기 상태로 업데이트
     */
    @PutMapping("/favorite")
    @Operation(
            summary = "즐겨찾기 상태로 업데이트",
            description = "동화 즐겨찾기 상태를 업데이트 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "즐겨찾기 상태를 성공적으로 변경했습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<Boolean>> isFavorite(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        Boolean newFavoriteStatus = bookService.toggleFavorite(profileId, bookId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_IS_FAVORITE, newFavoriteStatus));
    }

    /**
     * 동화 삭제
     */
    @DeleteMapping("/delete")
    @Operation(
            summary = "동화 삭제",
            description = "동화 삭제 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "동화를 성공적으로 삭제했습니다.")
            }
    )
    public ResponseEntity<ResponseDTO> deleteBook(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        bookService.deleteBook(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_DELETE_BOOK.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_BOOK, null));
    }

    /**
     * 현재 읽고 있는 페이지 업데이트
     */
    @PutMapping("/current")
    @Operation(
            summary = "현재 읽고 있는 페이지 업데이트",
            description = "동화 현재 페이지를 업데이트 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true),
                    @Parameter(name = "currentPage", in = ParameterIn.PATH, description = "현재 읽고 있는 페이지", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "현재 읽고 있는 페이지를 성공적으로 변경했습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<BookDTO>> updateCurrentPage(@RequestParam Integer profileId, @RequestParam Integer bookId, @RequestParam Integer currentPage) {
        BookDTO updatedBook = bookService.updateCurrentPage(profileId, bookId, currentPage);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_CURRENT_PAGE, updatedBook));
    }

    /**
     * 즐겨찾기 동화 필터링
     */
    @GetMapping("/favorites")
    @Operation(
            summary = "즐겨찾기 동화 필터링",
            description = "즐겨찾기 목록에 있는 동화 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "즐겨찾기 목록을 성공적으로 조회했습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getFavoriteBooks(@RequestParam Integer profileId) {
        List<BookListResponseDTO> favoriteBooks = bookService.getFavoriteBooks(profileId);
        if (favoriteBooks.isEmpty()) {
            return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST, favoriteBooks));
        }
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_FAVORITE_BOOKS, favoriteBooks));
    }

    /**
     * 읽고 있는 동화 필터링
     */
    @GetMapping("/reading")
    @Operation(
            summary = "읽고 있는 동화 필터링",
            description = "읽고 있는 동화 목록을 조회 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "읽고 있는 동화 목록을 성공적으로 조회했습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getReadingBooks(@RequestParam Integer profileId) {
        List<BookListResponseDTO> readingBooks = bookService.getReadingBooks(profileId);
        if (readingBooks.isEmpty()) {
            return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST, readingBooks));
        }
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_READING_BOOKS, readingBooks));
    }

    /***
     * 동화 퀴즈 생성
     */
    @PostMapping("/create/quiz")
    @Operation(
            summary = "동화 퀴즈 생성",
            description = "동화 퀴즈를 생성 API",
            parameters = {
                    @Parameter(name = "profileId", in = ParameterIn.PATH, description = "프로필 ID", required = true),
                    @Parameter(name = "bookId", in = ParameterIn.PATH, description = "동화 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "퀴즈가 성공적으로 생성되었습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<ResponseDTO<?>> createQuiz(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        QuizResponseDTO  createdBook = bookService.createQuiz(profileId, bookId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_QUIZ, createdBook));
    }
}
