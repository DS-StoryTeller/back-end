package com.cojac.storyteller.test.integration.profile;

import com.cojac.storyteller.common.amazon.AmazonS3Service;
import com.cojac.storyteller.profile.dto.PinCheckResultDTO;
import com.cojac.storyteller.profile.dto.PinNumberDTO;
import com.cojac.storyteller.profile.dto.ProfileDTO;
import com.cojac.storyteller.profile.dto.ProfilePhotoDTO;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.profile.service.ProfileService;
import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.user.entity.LocalUserEntity;
import com.cojac.storyteller.user.exception.InvalidPinNumberException;
import com.cojac.storyteller.user.exception.UserNotFoundException;
import com.cojac.storyteller.user.repository.LocalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 통합 테스트
 *
 * 여러 구성 요소(서비스, 데이터베이스 등) 간의 상호작용을 검증하기 위한 테스트 클래스입니다.
 * 실제 데이터베이스와 리포지토리를 사용하며, 외부 서비스는 모의 객체로 처리하여
 * 외부 의존성을 최소화합니다. 이를 통해 실질적인 통합 상황에서의 기능과 동작을 검증합니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProfileServiceIntegrationTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private LocalUserRepository localUserRepository;

    @MockBean
    private AmazonS3Service amazonS3Service;

    private LocalUserEntity localUserEntity;

    @BeforeEach
    public void setUp() {
        localUserEntity = LocalUserEntity.builder()
                .username("username")
                .encryptedPassword("password")
                .email("email.com")
                .role("ROLE_USER")
                .build();
        localUserEntity = localUserRepository.save(localUserEntity);
    }

    private static Stream<String> provideInvalidPinNumbers() {
        return Stream.of(null, "", "123", "abcd", "12345");
    }

    /**
     * 프로필 사진 가져오기
     */
    @Test
    @DisplayName("프로필 사진 가져오기 통합 테스트 - 성공")
    public void testGetProfilePhotos_Success() {
        when(amazonS3Service.getAllPhotos("profile/photos"))
                .thenReturn(List.of("photo1.jpg", "photo2.jpg"));

        List<ProfilePhotoDTO> photos = profileService.getProfilePhotos();

        assertThat(photos).isNotNull();
        assertThat(photos).hasSize(2);
        assertThat(photos.get(0).getImageUrl()).isEqualTo("photo1.jpg");
        assertThat(photos.get(1).getImageUrl()).isEqualTo("photo2.jpg");
    }

    @Test
    @DisplayName("프로필 사진 가져오기 통합 테스트 - 빈 리스트")
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
    @DisplayName("프로필 생성하기 통합 테스트 - 성공")
    public void testCreateProfile_Success() {
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Test name")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        ProfileDTO createdProfile = profileService.createProfile(newProfile);

        assertThat(createdProfile).isNotNull();
        assertThat(createdProfile.getName()).isEqualTo("Test name");

        ProfileEntity fetchedProfile = profileRepository.findById(createdProfile.getId())
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));
        assertThat(fetchedProfile.getName()).isEqualTo("Test name");
    }

    @Test
    @DisplayName("프로필 생성하기 통합 테스트 - 사용자 없음 예외")
    void testCreateProfile_UserNotFound() {
        // given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserId(-1);

        // when & then
        assertThrows(UserNotFoundException.class, () -> profileService.createProfile(profileDTO));
    }

    @ParameterizedTest
    @DisplayName("프로필 생성하기 통합 테스트 - 잘못된 핀 번호 형식 예외")
    @MethodSource("provideInvalidPinNumbers")
    void testCreateProfile_InvalidPinNumber(String pinNumber) {
        // given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserId(localUserEntity.getId());
        profileDTO.setPinNumber(pinNumber);

        // when & then
        assertThrows(InvalidPinNumberException.class, () -> profileService.createProfile(profileDTO));
    }

    /**
     * 암호된 프로필 비밀번호 체크하기
     */
    @Test
    @DisplayName("핀 번호 검증하기 통합 테스트 - 성공")
    public void testVerificationPinNumber_Success() {
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Test name")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        ProfileDTO createdProfile = profileService.createProfile(newProfile);

        PinNumberDTO pinNumberDTO = new PinNumberDTO("1234");
        PinCheckResultDTO result = profileService.verificationPinNumber(createdProfile.getId(), pinNumberDTO);

        assertThat(result.isValid()).isTrue();

        pinNumberDTO.setPinNumber("0000");
        result = profileService.verificationPinNumber(createdProfile.getId(), pinNumberDTO);
        assertThat(result.isValid()).isFalse();
    }

    @Test
    @DisplayName("핀 번호 검증하기 단위 테스트 - 프로필 없음 예외")
    void testVerificationPinNumber_ProfileNotFound() {
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Test name")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        profileService.createProfile(newProfile);

        // when
        int invalidProfileId = -1;
        PinNumberDTO pinNumberDTO = new PinNumberDTO("1234");

        // then
        assertThrows(ProfileNotFoundException.class, () -> profileService.verificationPinNumber(invalidProfileId, pinNumberDTO));
    }

    @Test
    @DisplayName("핀 번호 검증하기 통합 테스트 - 잘못된 핀 번호")
    void testVerificationPinNumber_InvalidPin() {
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Test name")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        ProfileDTO createdProfile = profileService.createProfile(newProfile);

        // when & then
        PinNumberDTO pinNumberDTO = new PinNumberDTO("4321");
        PinCheckResultDTO result = profileService.verificationPinNumber(createdProfile.getId(), pinNumberDTO);

        assertFalse(result.isValid());
    }

    /**
     * 프로필 업데이트
     */
    @Test
    @DisplayName("프로필 업데이트 통합 테스트 - 성공")
    public void testUpdateProfile_Success() {
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Initial Name")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        ProfileDTO createdProfile = profileService.createProfile(newProfile);

        ProfileDTO updatedProfileDTO = ProfileDTO.builder()
                .name("Updated Name")
                .pinNumber("5678")
                .build();

        ProfileDTO updatedProfile = profileService.updateProfile(createdProfile.getId(), updatedProfileDTO);

        assertThat(updatedProfile.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("프로필 업데이트 통합 테스트 - 프로필 없음 예외")
    void testUpdateProfile_ProfileNotFound() {
        // given
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Test name")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        profileService.createProfile(newProfile);

        // when
        int invalidProfileId = -1;

        // then
        assertThrows(ProfileNotFoundException.class, () -> profileService.updateProfile(invalidProfileId, newProfile));
    }

    @ParameterizedTest
    @DisplayName("프로필 업데이트 통합 테스트 - 잘못된 핀 번호 형식 예외")
    @MethodSource("provideInvalidPinNumbers")
    void testUpdateProfile_InvalidPinNumber(String pinNumber) {
        // given
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Test name")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        ProfileDTO createdProfile = profileService.createProfile(newProfile);

        // when
        ProfileDTO updateProfileDTO = ProfileDTO.builder()
                .pinNumber(pinNumber) // 형식이 잘못된 핀 번호
                .build();

        // then
        assertThrows(InvalidPinNumberException.class,
                () -> profileService.updateProfile(createdProfile.getId(), updateProfileDTO));
    }

    /**
     * 프로필 정보 조회하기
     */
    @Test
    @DisplayName("프로필 가져오기 통합 테스트 - 성공")
    void testGetProfile_Success() {
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Test Profile")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        ProfileDTO createdProfile = profileService.createProfile(newProfile);

        ProfileDTO fetchedProfile = profileService.getProfile(createdProfile.getId());

        assertThat(fetchedProfile.getName()).isEqualTo("Test Profile");
    }

    @Test
    @DisplayName("프로필 가져오기 - 프로필 없음 예외")
    void testGetProfile_ProfileNotFound() {
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Test Profile")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        profileService.createProfile(newProfile);

        // when & then
        int invalidProfileId = -1;
        assertThrows(ProfileNotFoundException.class, () -> profileService.getProfile(invalidProfileId));
    }

    /**
     * 프로필 목록 조회하기
     */
    @Test
    @DisplayName("프로필 목록 조회 통합 테스트 - 성공")
    public void testGetProfileList() {
        ProfileDTO profile1 = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Profile 1")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        ProfileDTO profile2 = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Profile 2")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        profileService.createProfile(profile1);
        profileService.createProfile(profile2);

        List<ProfileDTO> profiles = profileService.getProfileList(localUserEntity.getId());

        assertThat(profiles).hasSize(2);
        assertThat(profiles.stream().map(ProfileDTO::getName)).containsExactlyInAnyOrder("Profile 1", "Profile 2");
    }

    @Test
    @DisplayName("프로필 목록 조회 통합 테스트 - 유저를 찾을 수 없을 때 예외")
    void testGetProfileList_UserNotFound() {
        // given
        Integer invalidUserId = -1;

        // when & then
        assertThrows(UserNotFoundException.class, () -> profileService.getProfileList(invalidUserId));
    }

    /**
     * 프로필 삭제하기
     */
    @Test
    @DisplayName("프로필 삭제하기 통합 테스트 - 성공")
    public void testDeleteProfile() throws Exception {
        ProfileDTO newProfile = ProfileDTO.builder()
                .userId(localUserEntity.getId())
                .name("Profile to Delete")
                .birthDate(LocalDate.now())
                .pinNumber("1234")
                .build();

        ProfileDTO createdProfile = profileService.createProfile(newProfile);

        profileService.deleteProfile(createdProfile.getId());

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThrows(ProfileNotFoundException.class, () -> profileService.getProfile(createdProfile.getId()));
        });
    }

    @Test
    @DisplayName("프로필 삭제하기 통합 테스트 - 프로필을 찾을 수 없을 때 예외")
    void testDeleteProfile_ProfileNotFound() {
        // given
        Integer invalidProfileId = -1;

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> profileService.deleteProfile(invalidProfileId));
    }

}
