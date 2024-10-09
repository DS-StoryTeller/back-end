package com.cojac.storyteller.service;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.LocalUserEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.profile.PinCheckResultDTO;
import com.cojac.storyteller.dto.profile.PinNumberDTO;
import com.cojac.storyteller.dto.profile.ProfileDTO;
import com.cojac.storyteller.dto.profile.ProfilePhotoDTO;
import com.cojac.storyteller.exception.InvalidPinNumberException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.exception.UserNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.ProfileRepository;
import com.cojac.storyteller.repository.LocalUserRepository;
import com.cojac.storyteller.repository.batch.BatchProfileDelete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private LocalUserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BatchProfileDelete batchProfileDelete;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * S3에서 /profile/photos 경로에 있는 사진 목록 가져오기
     */
    @Test
    @DisplayName("프로필 사진 가져오기 - 성공")
    void testGetProfilePhotos_Success() {
        // given
        List<String> photoUrls = Arrays.asList("url1", "url2");
        when(amazonS3Service.getAllPhotos("profile/photos")).thenReturn(photoUrls);

        // when
        List<ProfilePhotoDTO> result = profileService.getProfilePhotos();

        // then
        assertEquals(2, result.size());
        assertEquals("url1", result.get(0).getImageUrl());
        assertEquals("url2", result.get(1).getImageUrl());
    }

    @Test
    @DisplayName("프로필 사진 가져오기 - 빈 리스트")
    void testGetProfilePhotos_EmptyList() {
        // given
        when(amazonS3Service.getAllPhotos("profile/photos")).thenReturn(List.of());

        // when
        List<ProfilePhotoDTO> result = profileService.getProfilePhotos();

        // then
        assertTrue(result.isEmpty());
    }

    /**
     * 프로필 생성하기
     */
    @Test
    @DisplayName("프로필 생성 - 성공")
    void testCreateProfile_Success() {
        // given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserId(1);
        profileDTO.setPinNumber("1234");

        LocalUserEntity userEntity = new LocalUserEntity();
        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));

        // Mocking BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
        when(encoder.encode("1234")).thenReturn("hashed1234");

        // Mocking ProfileRepository
        ProfileEntity profileEntity = new ProfileEntity();
        when(profileRepository.save(any(ProfileEntity.class))).thenReturn(profileEntity);

        // when
        ProfileDTO result = profileService.createProfile(profileDTO);

        // then
        assertNotNull(result);
        verify(profileRepository, times(1)).save(any(ProfileEntity.class));
    }

    @Test
    @DisplayName("프로필 생성 - 사용자 없음 예외")
    void testCreateProfile_UserNotFound() {
        // given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserId(1);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> profileService.createProfile(profileDTO));
    }

    @Test
    @DisplayName("프로필 생성 - 잘못된 핀 번호 예외")
    void testCreateProfile_InvalidPinNumber() {
        // given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserId(1);
        profileDTO.setPinNumber("12");

        // UserRepository에서 유효한 사용자 반환하도록 모킹
        LocalUserEntity userEntity = new LocalUserEntity();
        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));

        // when & then
        assertThrows(InvalidPinNumberException.class, () -> profileService.createProfile(profileDTO));
    }

    /**
     * 암호된 프로필 비밀번호 체크하기
     */
    @Test
    @DisplayName("핀 번호 검증 - 성공")
    void testVerificationPinNumber_Success() {
        // given
        Integer profileId = 1;
        PinNumberDTO pinNumberDTO = new PinNumberDTO();
        pinNumberDTO.setPinNumber("1234");

        // 실제로 BCryptPasswordEncoder를 사용하여 암호화된 핀 생성
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPin = encoder.encode("1234");

        ProfileEntity profileEntity = ProfileEntity.builder()
                .pinNumber(hashedPin)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profileEntity));

        // when
        PinCheckResultDTO result = profileService.verificationPinNumber(profileId, pinNumberDTO);

        // then
        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("핀 번호 검증 - 프로필 없음 예외")
    void testVerificationPinNumber_ProfileNotFound() {
        // given
        Integer profileId = 1;
        PinNumberDTO pinNumberDTO = new PinNumberDTO();
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> profileService.verificationPinNumber(profileId, pinNumberDTO));
    }

    @Test
    @DisplayName("핀 번호 검증 - 잘못된 핀 번호")
    void testVerificationPinNumber_InvalidPin() {
        // given
        Integer profileId = 1;
        PinNumberDTO pinNumberDTO = new PinNumberDTO();
        pinNumberDTO.setPinNumber("1234"); // 사용자가 입력한 핀 번호

        // 실제로 BCryptPasswordEncoder를 사용하여 암호화된 잘못된 핀 번호 생성
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPin = encoder.encode("5678");

        ProfileEntity profileEntity = ProfileEntity.builder()
                .pinNumber(hashedPin)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profileEntity));

        // when
        PinCheckResultDTO result = profileService.verificationPinNumber(profileId, pinNumberDTO);

        // then
        assertFalse(result.isValid());
    }

    /**
     * 프로필 업데이트
     */
    @Test
    @DisplayName("프로필 업데이트 - 성공")
    void testUpdateProfile_Success() {
        // given
        Integer profileId = 1;
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setPinNumber("1234");

        LocalUserEntity userEntity = new LocalUserEntity("username", "password", "email", "ROLE_USER");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPin = encoder.encode("oldPin");  // 이전 해시된 핀 번호

        ProfileEntity profileEntity = ProfileEntity.builder()
                .id(profileId)
                .user(userEntity)
                .pinNumber(hashedPin)
                .build();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profileEntity));

        // when
        ProfileDTO result = profileService.updateProfile(profileId, profileDTO);

        // then
        assertNotNull(result);
        assertEquals(profileId, result.getId());
        assertEquals(userEntity.getId(), result.getUserId());
    }

    @Test
    @DisplayName("프로필 업데이트 - 프로필 없음 예외")
    void testUpdateProfile_ProfileNotFound() {
        // given
        Integer profileId = 1;
        ProfileDTO profileDTO = new ProfileDTO();
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> profileService.updateProfile(profileId, profileDTO));
    }

    @Test
    @DisplayName("프로필 업데이트 - 잘못된 핀 번호 예외")
    void testUpdateProfile_InvalidPinNumber() {
        // given
        Integer profileId = 1;
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setPinNumber("12");  // 잘못된 핀 번호

        ProfileEntity profileEntity = new ProfileEntity();
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profileEntity));

        // when & then
        assertThrows(InvalidPinNumberException.class, () -> profileService.updateProfile(profileId, profileDTO));
    }

    /**
     * 프로필 정보 조회하기
     */
    @Test
    @DisplayName("프로필 가져오기 - 성공")
    void testGetProfile_Success() {
        // given
        Integer profileId = 1; // 테스트에 사용할 ID
        LocalUserEntity userEntity = new LocalUserEntity("username", "password", "email", "ROLE_USER");

        // ProfileEntity를 mock으로 생성
        ProfileEntity profileEntity = mock(ProfileEntity.class);
        when(profileEntity.getId()).thenReturn(profileId);
        when(profileEntity.getUser()).thenReturn(userEntity);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profileEntity));

        // when
        ProfileDTO result = profileService.getProfile(profileId);

        // then
        assertNotNull(result);
        assertEquals(profileId, result.getId());
        assertEquals(userEntity.getId(), result.getUserId());
    }

    @Test
    @DisplayName("프로필 가져오기 - 프로필 없음 예외")
    void testGetProfile_ProfileNotFound() {
        // given
        Integer profileId = 1;
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> profileService.getProfile(profileId));
    }

    /**
     * 프로필 목록 조회하기
     */
    @Test
    @DisplayName("프로필 목록 조회 - 성공")
    void testGetProfileList_Success() {
        // given
        Integer userId = 1;
        LocalUserEntity user = new LocalUserEntity("username", "password", "email", "ROLE_USER");

        ProfileEntity profile1 = ProfileEntity.builder().user(user).build();
        ProfileEntity profile2 = ProfileEntity.builder().user(user).build();
        List<ProfileEntity> profiles = Arrays.asList(profile1, profile2);

        // UserRepository와 ProfileRepository의 동작 모킹
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(profiles);

        // when
        List<ProfileDTO> result = profileService.getProfileList(userId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findById(userId);
        verify(profileRepository, times(1)).findByUser(user);
    }

    @Test
    @DisplayName("프로필 목록 조회 - 유저를 찾을 수 없을 때 예외")
    void testGetProfileList_UserNotFound() {
        // given
        Integer userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> profileService.getProfileList(userId));
    }

    /**
     * 프로필 삭제하기
     */
    @Test
    @DisplayName("프로필 삭제 - 성공")
    void testDeleteProfile_Success() throws Exception {
        // given
        Integer profileId = 1;

        ProfileEntity profile = ProfileEntity.builder()
                .build();

        BookEntity book = BookEntity.builder()
                .profile(profile)
                .coverImage("coverImageUrl")
                .build();

        profile.addBook(book);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByProfile(profile)).thenReturn(Collections.singletonList(book));

        // when
        profileService.deleteProfile(profileId);

        // then
        verify(bookRepository, times(1)).findByProfile(profile);
        verify(amazonS3Service, times(1)).deleteS3("coverImageUrl");
        verify(profileRepository, times(1)).findById(profileId);
        verify(batchProfileDelete, times(1)).deleteByProfileId(profileId);
    }

    @Test
    @DisplayName("프로필 삭제 - 프로필을 찾을 수 없을 때 예외")
    void testDeleteProfile_ProfileNotFound() {
        // given
        Integer profileId = 1;
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> profileService.deleteProfile(profileId));
    }

    /**
     * 여러 프로필 사진 업로드 테스트
     */
    @Test
    @DisplayName("여러 프로필 사진 업로드 - 성공")
    void testUploadMultipleFilesToS3_Success() throws IOException {
        // given
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        MultipartFile[] files = {file1, file2};

        // when
        profileService.uploadMultipleFilesToS3(files);

        // then
        verify(amazonS3Service, times(1)).uploadFileToS3(file1, "profile/photos");
        verify(amazonS3Service, times(1)).uploadFileToS3(file2, "profile/photos");
    }

    @Test
    @DisplayName("S3 업로드 실패 시 RuntimeException 발생")
    void testUploadMultipleFilesToS3_RuntimeException() throws IOException {
        // given
        MultipartFile file = mock(MultipartFile.class);
        MultipartFile[] files = {file};

        doThrow(new RuntimeException("S3 upload failed")).when(amazonS3Service).uploadFileToS3(file, "profile/photos");

        // when & then
        assertThrows(RuntimeException.class, () -> profileService.uploadMultipleFilesToS3(files));
    }

}