package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.book.BookDTO;
import com.cojac.storyteller.dto.book.CreateBookRequest;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<BookDTO>> createBook(@RequestBody CreateBookRequest request, @RequestParam Integer profileId) {
        BookDTO createdBook = bookService.createBook(request.getTitle(), request.getContent(), profileId);
        ResponseDTO<BookDTO> response = new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_BOOK, createdBook);
        return ResponseEntity.status(ResponseCode.SUCCESS_CREATE_BOOK.getStatus()).body(response);
    }
}