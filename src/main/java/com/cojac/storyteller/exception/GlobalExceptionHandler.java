package com.cojac.storyteller.exception;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.dto.response.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 컨트롤러 전역에서 발생하는 예외를 처리
@Slf4j
public class GlobalExceptionHandler {

    /**
     * User
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDuplicateUsernameException(final DuplicateUsernameException e) {
        log.error("handleDuplicateUsernameException : {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(ErrorCode.DUPLICATE_USERNAME.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.DUPLICATE_USERNAME));
    }

    /**
     * Book
     */
    @ExceptionHandler(ProfileNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleProfileNotFoundException(final ProfileNotFoundException e) {
        log.error("handleProfileNotFoundException : {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus().value())
                .body(new ErrorResponseDTO(e.getErrorCode()));
    }

    @ExceptionHandler(BookNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleBookNotFoundException(final BookNotFoundException e) {
        log.error("handleBookNotFoundException : {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus().value())
                .body(new ErrorResponseDTO(e.getErrorCode()));
    }

    /**
     * Page
     */
    @ExceptionHandler(PageNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handlePageNotFoundException(final PageNotFoundException e) {
        log.error("handlePageNotFoundException : {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus().value())
                .body(new ErrorResponseDTO(e.getErrorCode()));
    }

    /**
     * UnknownWord
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    protected ResponseEntity<ErrorResponseDTO> handleUnknownWordNotFoundException(final UnknownWordNotFoundException e) {
        log.error("handleDuplicateUsernameException : {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus().value())
                .body(new ErrorResponseDTO(e.getErrorCode()));
    }
}
