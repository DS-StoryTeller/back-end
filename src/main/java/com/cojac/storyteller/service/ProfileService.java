package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.SocialUserEntityEntity;
import com.cojac.storyteller.domain.LocalUserEntityEntity;
import com.cojac.storyteller.domain.UserEntity;
import com.cojac.storyteller.dto.profile.ProfileDTO;
import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
import com.cojac.storyteller.exception.SocialUserNotFoundException;
import com.cojac.storyteller.exception.UserNotFoundException;
import com.cojac.storyteller.repository.ProfileRepository;
import com.cojac.storyteller.repository.SocialUserRepository;
import com.cojac.storyteller.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final AmazonS3Service amazonS3Service;
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    private final ProfileRepository profileRepository;

    /**
     * S3에서 /profile/photos 경로에 있는 사진 목록 가져오기
     */
    public List<ProfilePhotoDTO> getProfilePhotos() {
        List<String> photoUrls = amazonS3Service.getAllPhotos("profile/photos");
        return photoUrls.stream()
                .map(ProfilePhotoDTO::new) // 각 URL을 DTO로 변환
                .collect(Collectors.toList());
    }

    /**
     * 프로필 생성하기
     */
    public ProfileDTO createProfile(ProfileDTO profileDTO) {

        // 사용자 아이디로 조회 및 예외 처리
        UserEntity user = userRepository.findById(profileDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 프로필 생성
        ProfileEntity profileEntity = new ProfileEntity(profileDTO.getName(), profileDTO.getBirthDate(), profileDTO.getImageUrl(), user);

        // 프로필 리포지토리 저장
        profileRepository.save(profileEntity);

        // DTO로 매핑
        return new ProfileDTO().mapEntityToDTO(profileEntity);
    }

}
