package com.cojac.storyteller.setting.service;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.setting.dto.SettingDTO;
import com.cojac.storyteller.setting.entity.SettingEntity;
import com.cojac.storyteller.setting.entity.enums.FontSize;
import com.cojac.storyteller.setting.entity.enums.ReadingSpeed;
import com.cojac.storyteller.user.entity.LocalUserEntity;
import com.cojac.storyteller.user.repository.LocalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 통합 테스트 클래스
 *
 * 이 클래스는 여러 구성 요소(서비스, 데이터베이스 등) 간의 상호작용을 검증하기 위한 통합 테스트를 포함합니다.
 *
 * 주요 특징:
 * - 실제 데이터베이스와 리포지토리를 사용하여 테스트를 수행합니다.
 * - 외부 서비스와의 의존성을 최소화하기 위해 모의 객체를 활용합니다.
 *
 * 통합 테스트는 여러 구성 요소 간의 상호작용을 검증하므로, 일반적으로 단위 테스트보다 느릴 수 있습니다.
 *
 * 테스트 전략:
 * - 간단한 기능이나 로직에 대한 테스트는 단위 테스트를 사용하십시오.
 * - 시스템의 전체적인 동작 및 상호작용을 검증하기 위해 통합 테스트를 활용하십시오.
 *
 */
@SpringBootTest
@Transactional
public class SettingServiceTest {

    @Autowired
    private SettingService settingService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private LocalUserRepository localUserRepository;

    private ProfileEntity profileEntity;
    private LocalUserEntity localUserEntity;
    private BookEntity bookEntity;

    @BeforeEach
    public void setup() {
        localUserEntity = LocalUserEntity.builder()
                .username("username")
                .encryptedPassword("password")
                .email("email.com")
                .role("ROLE_USER")
                .build();
        localUserEntity = localUserRepository.save(localUserEntity);

        profileEntity = ProfileEntity.builder()
                .name("Test name")
                .pinNumber("1234")
                .birthDate(LocalDate.of(2010, 1, 1))
                .user(localUserEntity)
                .build();
        profileEntity = profileRepository.save(profileEntity);

        bookEntity = BookEntity.builder()
                .title("Test Book")
                .coverImage("coverImage")
                .currentPage(1)
                .isReading(true)
                .isFavorite(false)
                .profile(profileEntity)
                .setting(new SettingEntity())
                .build();
        bookEntity = bookRepository.save(bookEntity);
    }

    /**
     * 설정 업데이트
     */
    @Test
    @DisplayName("설정 업데이트 통합 테스트 - 성공")
    public void testUpdateSetting_Success() {
        // Given
        SettingDTO settingDTO = new SettingDTO(FontSize.MEDIUM, ReadingSpeed.NORMAL);

        // When
        SettingDTO updatedSetting = settingService.updateSetting(profileEntity.getId(), bookEntity.getId(), settingDTO);

        // Then
        assertNotNull(updatedSetting);
        assertEquals(FontSize.MEDIUM, updatedSetting.getFontSize());
        assertEquals(ReadingSpeed.NORMAL, updatedSetting.getReadingSpeed());
    }

    @Test
    @DisplayName("설정 업데이트 통합 테스트 - 프로필이 존재하지 않을 때 예외 처리")
    public void testUpdateSetting_ProfileNotFound() {
        // Given
        SettingDTO settingDTO = new SettingDTO(FontSize.MEDIUM, ReadingSpeed.NORMAL);

        // When & Then
        assertThrows(ProfileNotFoundException.class, () ->
                settingService.updateSetting(-1, bookEntity.getId(), settingDTO));
    }

    @Test
    @DisplayName("설정 업데이트 통합 테스트 - 책이 존재하지 않을 때 예외 처리")
    public void testUpdateSetting_BookNotFound() {
        // Given
        SettingDTO settingDTO = new SettingDTO(FontSize.MEDIUM, ReadingSpeed.NORMAL);

        // When & Then
        assertThrows(BookNotFoundException.class, () ->
                settingService.updateSetting(profileEntity.getId(), -1, settingDTO));
    }

    /**
     * 설정 조회하기
     */
    @Test
    @DisplayName("설정 조회 통합 테스트 - 성공")
    public void testGetDetailSettings_Success() {
        // When
        SettingDTO settingDetail = settingService.getDetailSettings(profileEntity.getId(), bookEntity.getId());

        // Then
        assertNotNull(settingDetail);
    }

    @Test
    @DisplayName("설정 조회 통합 테스트 - 프로필이 존재하지 않을 때 예외 처리")
    public void testGetDetailSettings_ProfileNotFound() {
        // When & Then
        assertThrows(ProfileNotFoundException.class, () ->
                settingService.getDetailSettings(-1, bookEntity.getId()));
    }

    @Test
    @DisplayName("설정 조회 통합 테스트 - 책이 존재하지 않을 때 예외 처리")
    public void testGetDetailSettings_BookNotFound() {
        // When & Then
        assertThrows(BookNotFoundException.class, () ->
                settingService.getDetailSettings(profileEntity.getId(), -1));
    }
}
