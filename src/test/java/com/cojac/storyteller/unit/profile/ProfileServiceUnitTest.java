package com.cojac.storyteller.unit.profile;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.common.amazon.AmazonS3Service;
import com.cojac.storyteller.page.entity.PageEntity;
import com.cojac.storyteller.page.repository.PageRepository;
import com.cojac.storyteller.profile.dto.PinCheckResultDTO;
import com.cojac.storyteller.profile.dto.PinNumberDTO;
import com.cojac.storyteller.profile.dto.ProfileDTO;
import com.cojac.storyteller.profile.dto.ProfilePhotoDTO;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.profile.repository.batch.BatchProfileDelete;
import com.cojac.storyteller.profile.service.ProfileService;
import com.cojac.storyteller.unknownWord.repository.UnknownWordRepository;
import com.cojac.storyteller.user.entity.LocalUserEntity;
import com.cojac.storyteller.user.exception.InvalidPinNumberException;
import com.cojac.storyteller.user.exception.UserNotFoundException;
import com.cojac.storyteller.user.repository.LocalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 단위 테스트 클래스
 *
 * 이 클래스는 개별 단위(주로 서비스 또는 비즈니스 로직) 기능을 검증하기 위한 단위 테스트를 포함합니다.
 *
 * 주요 특징:
 * - 모의 객체(mock objects)를 사용하여 외부 의존성을 제거하고,테스트 대상 객체의 로직에만 집중합니다.
 * - 테스트의 독립성을 보장하여, 각 테스트가 서로에게 영향을 미치지 않도록 합니다.
 *
 * 테스트 전략:
 * - 간단한 기능이나 로직에 대한 테스트는 단위 테스트를 사용하십시오.
 * - 시스템의 전체적인 동작 및 상호작용을 검증하기 위해 통합 테스트를 활용하십시오.
 *
 * 참고: 단위 테스트는 실행 속도가 빠르며,
 *       전체 시스템의 동작보다는 개별 단위의 동작을 검증하는 데 중점을 둡니다.
 */
class ProfileServiceUnitTest {

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private LocalUserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PageRepository pageRepository;

    @Mock
    private UnknownWordRepository unknownWordRepository;

    @Mock
    private BatchProfileDelete batchProfileDelete;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static Stream<String> provideInvalidPinNumbers() {
        return Stream.of(null, "", "123", "abcd", "12345");
    }

    /**
     * 프로필 사진 가져오기
     */
    @Test
    @DisplayName("프로필 사진 가져오기 단위 테스트 - 성공")
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
    @DisplayName("프로필 사진 가져오기 단위 테스트 - 빈 리스트")
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
    @DisplayName("프로필 생성하기 단위 테스트 - 성공")
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
    @DisplayName("프로필 생성하기 단위 테스트 - 사용자 없음 예외")
    void testCreateProfile_UserNotFound() {
        // given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserId(1);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> profileService.createProfile(profileDTO));
    }

    @ParameterizedTest
    @DisplayName("프로필 생성하기 단위 테스트 - 잘못된 핀 번호 형식 예외")
    @MethodSource("provideInvalidPinNumbers")
    void testCreateProfile_InvalidPinNumber(String pinNumber) {
        // given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserId(1);
        profileDTO.setPinNumber(pinNumber);

        LocalUserEntity userEntity = new LocalUserEntity();
        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));

        // when & then
        assertThrows(InvalidPinNumberException.class, () -> profileService.createProfile(profileDTO));
    }

    /**
     * 암호된 프로필 비밀번호 체크하기
     */
    @Test
    @DisplayName("핀 번호 검증하기 단위 테스트 - 성공")
    void testVerificationPinNumber_Success() {
        // given
        Integer profileId = 1;
        PinNumberDTO pinNumberDTO = new PinNumberDTO();
        pinNumberDTO.setPinNumber("1234");

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
    @DisplayName("핀 번호 검증하기 단위 테스트 - 프로필 없음 예외")
    void testVerificationPinNumber_ProfileNotFound() {
        // given
        Integer profileId = 1;
        PinNumberDTO pinNumberDTO = new PinNumberDTO();
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> profileService.verificationPinNumber(profileId, pinNumberDTO));
    }

    @Test
    @DisplayName("핀 번호 검증하기 단위 테스트 - 잘못된 핀 번호")
    void testVerificationPinNumber_InvalidPin() {
        // given
        Integer profileId = 1;
        PinNumberDTO pinNumberDTO = new PinNumberDTO();
        pinNumberDTO.setPinNumber("1234");

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
    @DisplayName("프로필 업데이트 단위 테스트 - 성공")
    void testUpdateProfile_Success() {
        // given
        Integer profileId = 1;
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setPinNumber("1234");

        LocalUserEntity userEntity = new LocalUserEntity("username", "password", "email", "ROLE_USER");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPin = encoder.encode("oldPin");

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
    @DisplayName("프로필 업데이트 단위 테스트 - 프로필 없음 예외")
    void testUpdateProfile_ProfileNotFound() {
        // given
        Integer profileId = 1;
        ProfileDTO profileDTO = new ProfileDTO();
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> profileService.updateProfile(profileId, profileDTO));
    }

    @ParameterizedTest
    @DisplayName("프로필 업데이트 단위 테스트 - 잘못된 핀 번호 형식 예외")
    @MethodSource("provideInvalidPinNumbers")
    void testUpdateProfile_InvalidPinNumber(String pinNumber) {
        // given
        Integer profileId = 1;
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setPinNumber(pinNumber);  // 잘못된 핀 번호

        ProfileEntity profileEntity = new ProfileEntity();
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profileEntity));

        // when & then
        assertThrows(InvalidPinNumberException.class, () -> profileService.updateProfile(profileId, profileDTO));
    }

    /**
     * 프로필 정보 조회하기
     */
    @Test
    @DisplayName("프로필 가져오기 단위 테스트 - 성공")
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
    @DisplayName("프로필 목록 조회 단위 테스트 - 성공")
    void testGetProfileList_Success() {
        // given
        Integer userId = 1;
        LocalUserEntity user = new LocalUserEntity("username", "password", "email", "ROLE_USER");

        ProfileEntity profile1 = ProfileEntity.builder().user(user).build();
        ProfileEntity profile2 = ProfileEntity.builder().user(user).build();
        List<ProfileEntity> profiles = Arrays.asList(profile1, profile2);

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
    @DisplayName("프로필 목록 조회 단위 테스트 - 유저를 찾을 수 없을 때 예외")
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
    @DisplayName("프로필 삭제하기 단위 테스트 - 책만 존재하는 경우 - 성공")
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
    @DisplayName("프로필 삭제하기 단위 테스트 - 책과 페이지가 존재하는 경우 - 성공")
    void testDeleteProfile_withBooksAndPage_Success() throws Exception {
        // given
        Integer profileId = 1;

        ProfileEntity profile = ProfileEntity.builder()
                .build();

        BookEntity book = BookEntity.builder()
                .profile(profile)
                .coverImage("coverImageUrl")
                .build();

        PageEntity page1 = PageEntity.builder()
                .pageNumber(1)
                .book(book)
                .build();

        PageEntity page2 = PageEntity.builder()
                .pageNumber(2)
                .book(book)
                .build();

        profile.addBook(book); // Adding book to profile

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByProfile(profile)).thenReturn(Collections.singletonList(book));

        // When
        profileService.deleteProfile(profileId);

        // Then
        verify(profileRepository, times(1)).findById(profileId);
        verify(bookRepository, times(1)).findByProfile(profile);
        verify(batchProfileDelete, times(1)).deleteByProfileId(profileId);
    }


    @Test
    @DisplayName("프로필 삭제하기 단위 테스트 - 책과 페이지가 없는 경우 - 성공")
    void testDeleteProfile_NoBooksOrPages_Success() throws Exception {
        // given
        Integer profileId = 1;
        ProfileEntity profile = new ProfileEntity();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByProfile(profile)).thenReturn(Collections.emptyList());

        // when
        profileService.deleteProfile(profileId);

        // then
        verify(bookRepository, times(1)).findByProfile(profile);
        verify(amazonS3Service, times(0)).deleteS3(anyString());
        verify(profileRepository, times(1)).findById(profileId);
        verify(batchProfileDelete, times(1)).deleteByProfileId(profileId);
    }


    @Test
    @DisplayName("프로필 삭제하기 단위 테스트 - 프로필을 찾을 수 없을 때 예외")
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