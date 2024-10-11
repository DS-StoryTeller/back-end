package com.cojac.storyteller.response.dto;

import com.cojac.storyteller.response.code.ResponseCode;
import lombok.Data;

@Data
public class ResponseDTO<T> {
    private Integer status;
    private String code;
    private String message;
    private T data;

    public ResponseDTO(ResponseCode responseCode, T data) {
        this.status = responseCode.getStatus().value();
        this.code = responseCode.name();
        this.message = responseCode.getMessage();
        this.data = data;
    }
}
