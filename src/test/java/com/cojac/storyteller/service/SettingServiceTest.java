package com.cojac.storyteller.service;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.SettingEntity;
import com.cojac.storyteller.dto.setting.SettingDTO;
import com.cojac.storyteller.exception.BookNotFoundException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingServiceTest {

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
    @DisplayName("설정 업데이트 - 성공")
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
    @DisplayName("설정 업데이트 - 프로필 없음 예외")
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
    @DisplayName("설정 업데이트 - 책 없음 예외")
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
    @DisplayName("설정 조회 - 성공")
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
    @DisplayName("설정 조회 - 프로필 없음 예외")
    void testGetDetailSettings_ProfileNotFound() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> settingService.getDetailSettings(profileId, bookId));
    }

    @Test
    @DisplayName("설정 조회 - 책 없음 예외")
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
