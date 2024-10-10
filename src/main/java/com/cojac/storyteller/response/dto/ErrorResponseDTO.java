package com.cojac.storyteller.response.dto;

import com.cojac.storyteller.response.code.ErrorCode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDTO {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String code;
    private String message;

    public ErrorResponseDTO(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public ErrorResponseDTO(ErrorCode errorCode, String message) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.name();
        this.message = message;
    }
}
