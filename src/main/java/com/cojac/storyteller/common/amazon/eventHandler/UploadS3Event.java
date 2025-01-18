package com.cojac.storyteller.common.amazon.eventHandler;

/**
 * S3에서 업로드하면, S3 객체의 정보를 이용해 이벤트 생성
 * 추후에 삭제할 때 이번트를 이용해 정보 획득
 * @param objectPath 업로드된 이미지의 URL
 */
public record UploadS3Event(
        String objectPath
) {
    public String getFileName() {
        return objectPath.substring(objectPath.lastIndexOf("/") + 1);
    }
}

