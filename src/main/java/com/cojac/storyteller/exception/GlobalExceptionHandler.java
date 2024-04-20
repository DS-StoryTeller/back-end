package com.cojac.storyteller.exception;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.dto.response.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice //컨트롤러 전역에서 발생하는 예외 throw
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

}
