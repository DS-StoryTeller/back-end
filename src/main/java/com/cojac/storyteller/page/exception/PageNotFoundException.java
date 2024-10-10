package com.cojac.storyteller.page.exception;

import com.cojac.storyteller.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PageNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
}
