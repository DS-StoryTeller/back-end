package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.controller.swagger.BookControllerDocs;
import com.cojac.storyteller.dto.book.*;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController implements BookControllerDocs {

    private final BookService bookService;

    /**
     * 동화 내용 생성
     */
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<?>> createBook(@RequestBody CreateBookRequest request, @RequestParam Integer profileId) {
        BookDTO  createdBook = bookService.createBook(request.getPrompt(), profileId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_BOOK.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_BOOK, createdBook));
    }

    /**
     * 동화 목록 조회
     */
    @GetMapping("/booklist")
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getBookList(@RequestParam Integer profileId) {
        List<BookListResponseDTO> books = bookService.getAllBooks(profileId);
        ResponseCode responseCode = books.isEmpty() ? ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST : ResponseCode.SUCCESS_RETRIEVE_BOOKS;
        return ResponseEntity
                .status(responseCode.getStatus().value())
                .body(new ResponseDTO<>(responseCode, books));
    }

    /**
     * 동화 세부 정보 조회
     */
    @GetMapping("/detail")
    public ResponseEntity<ResponseDTO<BookDetailResponseDTO>> getBookDetail(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        BookDetailResponseDTO bookDetail = bookService.getBookDetail(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_RETRIEVE_BOOK_DETAILS.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_BOOK_DETAILS, bookDetail));
    }

    /**
     * 즐겨찾기 상태로 업데이트
     */
    @PutMapping("/favorite")
    public ResponseEntity<ResponseDTO<Boolean>> isFavorite(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        Boolean newFavoriteStatus = bookService.toggleFavorite(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPDATE_IS_FAVORITE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_IS_FAVORITE, newFavoriteStatus));
    }

    /**
     * 동화 삭제
     */
    @DeleteMapping("/delete")
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
    public ResponseEntity<ResponseDTO<BookDTO>> updateCurrentPage(@RequestParam Integer profileId, @RequestParam Integer bookId, @RequestParam Integer currentPage) {
        BookDTO updatedBook = bookService.updateCurrentPage(profileId, bookId, currentPage);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_UPDATE_CURRENT_PAGE.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_CURRENT_PAGE, updatedBook));
    }

    /**
     * 즐겨찾기 동화 필터링
     */
    @GetMapping("/favorites")
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getFavoriteBooks(@RequestParam Integer profileId) {
        List<BookListResponseDTO> favoriteBooks = bookService.getFavoriteBooks(profileId);
        ResponseCode responseCode = favoriteBooks.isEmpty() ? ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST : ResponseCode.SUCCESS_RETRIEVE_FAVORITE_BOOKS;
        return ResponseEntity
                .status(responseCode.getStatus().value())
                .body(new ResponseDTO<>(responseCode, favoriteBooks));
    }

    /**
     * 읽고 있는 동화 필터링
     */
    @GetMapping("/reading")
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getReadingBooks(@RequestParam Integer profileId) {
        List<BookListResponseDTO> readingBooks = bookService.getReadingBooks(profileId);
        ResponseCode responseCode = readingBooks.isEmpty() ? ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST : ResponseCode.SUCCESS_RETRIEVE_READING_BOOKS;
        return ResponseEntity
                .status(responseCode.getStatus().value())
                .body(new ResponseDTO<>(responseCode, readingBooks));
    }

    /**
     * 동화 퀴즈 생성
     */
    @PostMapping("/create/quiz")
    public ResponseEntity<ResponseDTO<?>> createQuiz(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        QuizResponseDTO  createdBook = bookService.createQuiz(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_QUIZ.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_QUIZ, createdBook));
    }
}
