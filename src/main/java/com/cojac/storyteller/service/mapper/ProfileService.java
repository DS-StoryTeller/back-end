package com.cojac.storyteller.service.mapper;

import com.cojac.storyteller.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final AmazonS3Service amazonS3Service;

    /**
     * AmazonS3 작동 확인을 위한 테스트 코드입니다. 추후에 프로필 로직에 맞게 수정할 예정
     * @param name
     * @param file
     */
    public void createProfile(String name, MultipartFile file) {
        String url = "";
        if(file != null)  url = amazonS3Service.uploadFileToS3(file, "static/profile-image");
    }
}
