package com.cojac.storyteller.unknownWord.exception;

import com.cojac.storyteller.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UnknownWordNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
}
