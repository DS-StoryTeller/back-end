package com.cojac.storyteller.service;

import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
import com.cojac.storyteller.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final AmazonS3Service amazonS3Service;

    /**
     * S3에서 /profile/photos 경로에 있는 사진 목록 가져오기
     */
    //
    public List<ProfilePhotoDTO> getProfilePhotos() {
        List<String> photoUrls = amazonS3Service.getAllPhotos("profile/photos");
        return photoUrls.stream()
                .map(ProfilePhotoDTO::new) // 각 URL을 DTO로 변환
                .collect(Collectors.toList());
    }

    /**
     * AmazonS3 작동 확인을 위한 테스트 코드입니다. 추후에 프로필 로직에 맞게 수정할 예정
     * @param name
     * @param file
     */
    public void createProfile(String name, MultipartFile file) {
        String url = "";
        if(file != null)  url = amazonS3Service.uploadFileToS3(file, "profile/photos");
    }
}
