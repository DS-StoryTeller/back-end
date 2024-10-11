package com.cojac.storyteller.book.controller;

import com.cojac.storyteller.book.service.BookService;
import com.cojac.storyteller.book.dto.*;
import com.cojac.storyteller.response.code.ResponseCode;
import com.cojac.storyteller.common.swagger.BookControllerDocs;
import com.cojac.storyteller.response.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profiles/{profileId}/books")
@RequiredArgsConstructor
public class BookController implements BookControllerDocs {

    private final BookService bookService;

    /**
     * 동화 내용 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDTO> createBook(@PathVariable Integer profileId, @RequestBody CreateBookRequest request) {
        BookDTO createdBook = bookService.createBook(request.getPrompt(), profileId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_BOOK.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_BOOK, createdBook));
    }

    /**
     * 동화 목록 조회
     */
    @GetMapping
    public ResponseEntity<ResponseDTO> getBookList(
            @PathVariable Integer profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<BookListResponseDTO> books = bookService.getBooksPage(profileId, pageable);
        ResponseCode responseCode = books.isEmpty() ? ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST : ResponseCode.SUCCESS_RETRIEVE_BOOKS;
        return ResponseEntity
                .status(responseCode.getStatus().value())
                .body(new ResponseDTO<>(responseCode, books));
    }

    /**
     * 동화 세부 정보 조회
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<ResponseDTO<BookDetailResponseDTO>> getBookDetail(@PathVariable Integer profileId, @PathVariable Integer bookId) {
        BookDetailResponseDTO bookDetail = bookService.getBookDetail(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_RETRIEVE_BOOK_DETAILS.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_BOOK_DETAILS, bookDetail));
    }

    /**
     * 즐겨찾기 상태로 업데이트
     */
    @PutMapping("/{bookId}/favorite")
    public ResponseEntity<ResponseDTO<Boolean>> isFavorite(@PathVariable Integer profileId, @PathVariable Integer bookId) {
        Boolean newFavoriteStatus = bookService.toggleFavorite(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPDATE_IS_FAVORITE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_IS_FAVORITE, newFavoriteStatus));
    }

    /**
     * 동화 삭제
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<ResponseDTO> deleteBook(@PathVariable Integer profileId, @PathVariable Integer bookId) throws Exception {
        bookService.deleteBook(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_DELETE_BOOK.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_BOOK, null));
    }

    /**
     * 현재 읽고 있는 페이지 업데이트
     */
    @PutMapping("/{bookId}/current")
    public ResponseEntity<ResponseDTO<BookDTO>> updateCurrentPage(@PathVariable Integer profileId, @PathVariable Integer bookId, @RequestParam Integer currentPage) {
        BookDTO updatedBook = bookService.updateCurrentPage(profileId, bookId, currentPage);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPDATE_CURRENT_PAGE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_CURRENT_PAGE, updatedBook));
    }

    /**
     * 즐겨찾기 동화 조회
     */
    @GetMapping("/favorites")
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getFavoriteBooks(
            @PathVariable Integer profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort[0]).ascending());

        List<BookListResponseDTO> favoriteBooks = bookService.getFavoriteBooks(profileId, pageable);
        ResponseCode responseCode = favoriteBooks.isEmpty() ? ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST : ResponseCode.SUCCESS_RETRIEVE_FAVORITE_BOOKS;
        return ResponseEntity
                .status(responseCode.getStatus().value())
                .body(new ResponseDTO<>(responseCode, favoriteBooks));
    }

    /**
     * 읽고 있는 동화 조회
     */
    @GetMapping("/reading")
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getReadingBooks(
            @PathVariable Integer profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort[0]).ascending());

        List<BookListResponseDTO> readingBooks = bookService.getReadingBooks(profileId, pageable);
        ResponseCode responseCode = readingBooks.isEmpty() ? ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST : ResponseCode.SUCCESS_RETRIEVE_READING_BOOKS;
        return ResponseEntity
                .status(responseCode.getStatus().value())
                .body(new ResponseDTO<>(responseCode, readingBooks));
    }

    /**
     * 동화 퀴즈 생성
     */
    @PostMapping("/{bookId}/quiz")
    public ResponseEntity<ResponseDTO> createQuiz(@PathVariable Integer profileId, @PathVariable Integer bookId) {
        QuizResponseDTO createdBook = bookService.createQuiz(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_QUIZ.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_QUIZ, createdBook));
    }
}
