package com.cojac.storyteller.common.amazon.dto;

import lombok.Getter;

@Getter
public class S3DeleteFileDTO {

    private String filePath; // 파일 경로
    private String fileUid; // 파일 식별자

    /**
     * S3DeleteFileDTO 객체 생성
     */
    public static S3DeleteFileDTO create(String filePath, String fileUid) {
        S3DeleteFileDTO deleteFileDTO = new S3DeleteFileDTO();
        deleteFileDTO.filePath = filePath;
        deleteFileDTO.fileUid = fileUid;
        return deleteFileDTO;
    }
}
