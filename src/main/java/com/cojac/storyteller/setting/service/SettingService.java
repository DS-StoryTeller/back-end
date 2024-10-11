package com.cojac.storyteller.setting.service;

import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.setting.dto.SettingDTO;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.setting.entity.SettingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;

    /**
     * 설정 업데이트
     */
    @Transactional
    public SettingDTO updateSetting(Integer profileId, Integer bookId, SettingDTO settingDTO) {
        // 프로필이 존재하는지 확인
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 해당 프로필에 해당하는 책 가져오기(페치 조인으로 book + setting 정보 가져오기)
        BookEntity book = bookRepository.findByIdAndProfileWithSetting(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 설정 정보 업데이트
        SettingEntity setting = book.getSetting();
        setting.updateSetting(settingDTO);

        return SettingDTO.toDto(setting);
    }

    /**
     * 설정 조회하기
     */
    public SettingDTO getDetailSettings(Integer profileId, Integer bookId) {
        // 프로필이 존재하는지 확인
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 해당 프로필에 해당하는 책 가져오기(페치 조인으로 book + setting 정보 가져오기)
        BookEntity book = bookRepository.findByIdAndProfileWithSetting(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 설정 정보 업데이트
        return SettingDTO.toDto(book.getSetting());
    }
}
