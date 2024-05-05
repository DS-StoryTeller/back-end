package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.book.BookDTO;
import com.cojac.storyteller.dto.book.CreateBookRequest;
import com.cojac.storyteller.dto.response.ErrorResponseDTO;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.exception.ProfileNotFoundException;
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
    public ResponseEntity<ResponseDTO> createBook(@RequestBody CreateBookRequest request, @RequestParam Integer profileId) {
        BookDTO createdBook = bookService.createBook(request.getTitle(), request.getContent(), profileId);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_CREATE_BOOK.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_CREATE_BOOK, createdBook));
    }
}
