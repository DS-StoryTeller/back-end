package com.cojac.storyteller.unit.setting;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.setting.dto.SettingDTO;
import com.cojac.storyteller.setting.entity.SettingEntity;
import com.cojac.storyteller.setting.service.SettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
@ExtendWith(MockitoExtension.class)
class SettingServiceUnitTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private SettingService settingService;

    private ProfileEntity profile;
    private BookEntity book;
    private SettingEntity setting;

    @BeforeEach
    void setUp() {
        profile = ProfileEntity.builder().id(1).birthDate(LocalDate.of(2015, 1, 1)).build();
        setting = new SettingEntity();
        book = BookEntity.builder().id(1).profile(profile).title("Test Book").setting(setting).build();
    }

    /**
     * 설정 업데이트
     */
    @Test
    @DisplayName("설정 업데이트 단위 테스트 - 성공")
    void testUpdateSetting_Success() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;
        SettingDTO settingDTO = new SettingDTO();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfileWithSetting(bookId, profile)).thenReturn(Optional.of(book));

        // when
        SettingDTO result = settingService.updateSetting(profileId, bookId, settingDTO);

        // then
        assertNotNull(result);
        verify(profileRepository, times(1)).findById(profileId);
        verify(bookRepository, times(1)).findByIdAndProfileWithSetting(bookId, profile);
    }

    @Test
    @DisplayName("설정 업데이트 단위 테스트 - 프로필이 존재하지 않을 때 예외 처리")
    void testUpdateSetting_ProfileNotFound() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;
        SettingDTO settingDTO = new SettingDTO();

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> settingService.updateSetting(profileId, bookId, settingDTO));
    }

    @Test
    @DisplayName("설정 업데이트 단위 테스트 - 책이 존재하지 않을 때 예외 처리")
    void testUpdateSetting_BookNotFound() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;
        SettingDTO settingDTO = new SettingDTO();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfileWithSetting(bookId, profile)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> settingService.updateSetting(profileId, bookId, settingDTO));
    }

    /**
     * 설정 조회하기
     */
    @Test
    @DisplayName("설정 조회 단위 테스트 - 성공")
    void testGetDetailSettings_Success() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfileWithSetting(bookId, profile)).thenReturn(Optional.of(book));

        // when
        SettingDTO result = settingService.getDetailSettings(profileId, bookId);

        // then
        assertNotNull(result);
        verify(bookRepository, times(1)).findByIdAndProfileWithSetting(bookId, profile);
        verify(profileRepository, times(1)).findById(profileId);
    }

    @Test
    @DisplayName("설정 조회 단위 테스트 - 프로필이 존재하지 않을 때 예외 처리")
    void testGetDetailSettings_ProfileNotFound() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> settingService.getDetailSettings(profileId, bookId));
    }

    @Test
    @DisplayName("설정 조회 단위 테스트 - 책이 존재하지 않을 때 예외 처리")
    void testGetDetailSettings_BookNotFound() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfileWithSetting(bookId, profile)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> settingService.getDetailSettings(profileId, bookId));
    }
}
