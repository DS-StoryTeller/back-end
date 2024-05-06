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
    SUCCESS_LOGIN(HttpStatus.OK, "로그인을 성공했습니다."),
    SUCCESS_REISSUE(HttpStatus.OK, "토큰 재발급을 성공했습니다."),
    SUCCESS_TEST(HttpStatus.OK, "테스트를 성공했습니다."),
    SUCCESS_LOGOUT(HttpStatus.OK, "로그아웃을 성공했습니다."),
  
    /**
     * Book
     */
    SUCCESS_CREATE_BOOK(HttpStatus.CREATED, "동화가 성공적으로 생성되었습니다."),
    SUCCESS_RETRIEVE_BOOKS(HttpStatus.OK, "책 목록을 성공적으로 조회했습니다.");

    private final HttpStatus status;
    private final String message;
}