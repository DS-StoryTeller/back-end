package com.cojac.storyteller.exception;

import com.cojac.storyteller.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DuplicateUsernameException extends RuntimeException {

    private final ErrorCode errorCode;
}
