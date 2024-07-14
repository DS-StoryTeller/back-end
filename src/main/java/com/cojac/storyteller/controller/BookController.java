package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.book.*;
import com.cojac.storyteller.dto.response.ErrorResponseDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<List<QuizResponseDTO>>> createBook(@RequestBody CreateBookRequest request, @RequestParam Integer profileId) {
        List<QuizResponseDTO>  createdBook = bookService.createBook(request.getPrompt(), profileId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_BOOK_AND_QUIZ, createdBook));
    }

    @GetMapping("/booklist")
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getBookList(@RequestParam Integer profileId) {
        List<BookListResponseDTO> books = bookService.getAllBooks(profileId);
        if (books.isEmpty()) {
            return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST, books));
        }
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_BOOKS, books));
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseDTO<BookDetailResponseDTO>> getBookDetail(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        BookDetailResponseDTO bookDetail = bookService.getBookDetail(profileId, bookId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_BOOK_DETAILS, bookDetail));
    }

    @PutMapping("/favorite")
    public ResponseEntity<ResponseDTO<Boolean>> isFavorite(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        Boolean newFavoriteStatus = bookService.toggleFavorite(profileId, bookId);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_IS_FAVORITE, newFavoriteStatus));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDTO> deleteBook(@RequestParam Integer profileId, @RequestParam Integer bookId) {
        bookService.deleteBook(profileId, bookId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_DELETE_BOOK.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_DELETE_BOOK, null));
    }

    // 현재 읽고 있는 페이지 업데이트
    @PutMapping("/current")
    public ResponseEntity<ResponseDTO<BookDTO>> updateCurrentPage(@RequestParam Integer profileId, @RequestParam Integer bookId, @RequestParam Integer currentPage) {
        BookDTO updatedBook = bookService.updateCurrentPage(profileId, bookId, currentPage);
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_UPDATE_CURRENT_PAGE, updatedBook));
    }

    // 즐겨찾기 책 필터링 엔드포인트
    @GetMapping("/favorites")
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getFavoriteBooks(@RequestParam Integer profileId) {
        List<BookListResponseDTO> favoriteBooks = bookService.getFavoriteBooks(profileId);
        if (favoriteBooks.isEmpty()) {
            return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST, favoriteBooks));
        }
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_FAVORITE_BOOKS, favoriteBooks));
    }

    // 읽고 있는 책 필터링 엔드포인트
    @GetMapping("/reading")
    public ResponseEntity<ResponseDTO<List<BookListResponseDTO>>> getReadingBooks(@RequestParam Integer profileId) {
        List<BookListResponseDTO> readingBooks = bookService.getReadingBooks(profileId);
        if (readingBooks.isEmpty()) {
            return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_EMPTY_LIST, readingBooks));
        }
        return ResponseEntity.ok(new ResponseDTO<>(ResponseCode.SUCCESS_RETRIEVE_READING_BOOKS, readingBooks));
    }
}
