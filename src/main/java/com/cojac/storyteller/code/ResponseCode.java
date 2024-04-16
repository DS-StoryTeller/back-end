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
    SUCCESS_JOIN(HttpStatus.OK, "회원가입을 성공했습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
