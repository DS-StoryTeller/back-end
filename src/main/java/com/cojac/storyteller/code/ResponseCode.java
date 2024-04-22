package com.cojac.storyteller.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ResponseCode {
    /**
     * User
     */
    SUCCESS_REGISTER(HttpStatus.OK, "회원가입을 성공했습니다."),

    /**
     * Book
     */
    SUCCESS_CREATE_BOOK(HttpStatus.CREATED, "동화가 성공적으로 생성되었습니다.");

    private final HttpStatus status;
    private final String message;
}