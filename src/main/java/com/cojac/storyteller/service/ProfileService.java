package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.UserEntity;
import com.cojac.storyteller.dto.profile.PinNumberDTO;
import com.cojac.storyteller.dto.profile.ProfileDTO;
import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
import com.cojac.storyteller.exception.InvalidPinNumberException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.exception.UserNotFoundException;
import com.cojac.storyteller.repository.ProfileRepository;
import com.cojac.storyteller.repository.SocialUserRepository;
import com.cojac.storyteller.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        // 핀 번호 유효성 검증
        String pinNumber = profileDTO.getPinNumber();
        if (pinNumber == null || pinNumber.length() != 4 || !pinNumber.matches("\\d+")) {
            throw new InvalidPinNumberException(ErrorCode.INVALID_PIN_NUMBER);
        }

        // 핀 번호 암호화
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPin = encoder.encode(pinNumber);

        // 프로필 생성
        ProfileEntity profileEntity = new ProfileEntity(
                profileDTO.getName(),
                profileDTO.getBirthDate(),
                profileDTO.getImageUrl(),
                hashedPin,
                user);

        // 프로필 리포지토리 저장
        profileRepository.save(profileEntity);

        // DTO로 매핑
        return new ProfileDTO().mapEntityToDTO(profileEntity);
    }

    /**
     * 암호된 프로필 비밀번호 체크하기
     */
    public void checkPinNumber(Integer profileId, PinNumberDTO pinNumberDTO) {

        // 프로필 아이디로 프로필을 찾아옵니다.
        ProfileEntity profileEntity = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // DB에 저장된 암호화된 핀 번호를 가져옵니다.
        String hashedPinFromDB = profileEntity.getPinNumber();

        // 입력된 핀 번호를 가져옵니다.
        String inputPin = pinNumberDTO.getPinNumber();

        // BCryptPasswordEncoder를 사용하여 비밀번호를 검증합니다.
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(inputPin, hashedPinFromDB)) {
            throw new InvalidPinNumberException(ErrorCode.INVALID_PIN_NUMBER); // 비밀번호가 일치하지 않으면 예외를 던집니다.
        }
    }


}
