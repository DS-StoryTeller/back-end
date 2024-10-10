package com.cojac.storyteller.book.exception;

import com.cojac.storyteller.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public class BookNotFoundException extends RuntimeException{
    private final ErrorCode errorCode;
}
