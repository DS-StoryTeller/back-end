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
  
    /**H
     * Book
     */
    SUCCESS_CREATE_BOOK_AND_QUIZ(HttpStatus.CREATED, "동화와 퀴즈가 성공적으로 생성되었습니다."),
    SUCCESS_RETRIEVE_BOOKS(HttpStatus.OK, "책 목록을 성공적으로 조회했습니다."),
    SUCCESS_RETRIEVE_BOOK_DETAILS(HttpStatus.OK, "책 세부 정보를 성공적으로 조회했습니다."),
    SUCCESS_UPDATE_IS_FAVORITE(HttpStatus.OK, "즐겨찾기 상태를 성공적으로 변경했습니다."),
    SUCCESS_DELETE_BOOK(HttpStatus.OK, "책을 성공적으로 삭제했습니다."),
    SUCCESS_RETRIEVE_FAVORITE_BOOKS(HttpStatus.OK, "즐겨찾기 목록을 성공적으로 조회했습니다."),
    SUCCESS_RETRIEVE_READING_BOOKS(HttpStatus.OK, "읽고 있는 책 목록을 성공적으로 조회했습니다."),

    /**
     * Page
     */
    SUCCESS_RETRIEVE_PAGE_DETAILS(HttpStatus.OK, "페이지 세부 정보를 성공적으로 조회했습니다."),
    SUCCESS_UPDATE_PAGE_IMAGE(HttpStatus.OK, "페이지 이미지를 성공적으로 변경했습니다."),

    /**
     * UnknownWord
     */
    SUCCESS_CREATE_UNKNOWNWORD(HttpStatus.CREATED, "단어가 성공적으로 저장되었습니다"),
    SUCCESS_DELETE_UNKNOWNWORD(HttpStatus.OK, "단어가 성공적으로 삭제되었습니다"),

    /**
     * Setting
     */
    SUCCESS_UPDATE_SETTING(HttpStatus.OK, "책 설정을 성공적으로 변경했습니다"),
    SUCCESS_RETRIEVE_SETTING(HttpStatus.OK, "책 설정을 성공적으로 조회했습니다"),

    /**
     * Custom status for empty data lists
     */
    SUCCESS_RETRIEVE_EMPTY_LIST(HttpStatus.OK, "데이터 조회를 성공했으나, 목록이 비어 있습니다.");

    private final HttpStatus status;
    private final String message;
}