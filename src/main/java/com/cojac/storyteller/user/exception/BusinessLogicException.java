package com.cojac.storyteller.user.exception;

import com.cojac.storyteller.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessLogicException extends RuntimeException{
    private final ErrorCode errorCode;
}
