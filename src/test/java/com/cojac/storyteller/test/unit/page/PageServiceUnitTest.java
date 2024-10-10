package com.cojac.storyteller.test.unit.page;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.page.dto.PageDetailResponseDTO;
import com.cojac.storyteller.page.entity.PageEntity;
import com.cojac.storyteller.page.exception.PageNotFoundException;
import com.cojac.storyteller.page.repository.PageRepository;
import com.cojac.storyteller.page.service.PageService;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 단위 테스트
 *
 * 개별 메서드 및 클래스의 동작을 검증하기 위한 테스트 클래스입니다.
 * 각 테스트는 특정 기능이나 비즈니스 로직을 독립적으로 확인하며,
 * 외부 의존성을 최소화하기 위해 모의 객체를 사용합니다.
 */
@ExtendWith(MockitoExtension.class)
class PageServiceUnitTest {

    @Mock
    private PageRepository pageRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private PageService pageService;

    private ProfileEntity profile;
    private BookEntity book;
    private PageEntity page;

    @BeforeEach
    void setUp() {
        profile = ProfileEntity.builder().id(1).build();
        book = BookEntity.builder().id(1).profile(profile).title("Test Book").build();
        page = PageEntity.builder().id(1).pageNumber(1).image("imageUrl").content("Page content").build();
    }

    /**
     * 페이지 세부 정보 가져오기
     */
    @Test
    @DisplayName("페이지 세부 정보 가져오기 단위 테스트 - 성공")
    void testGetPageDetail_Success() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;
        Integer pageNum = 1;

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(bookId, profile)).thenReturn(Optional.of(book));
        when(pageRepository.findPageWithUnknownWords(book, pageNum)).thenReturn(Optional.of(page));

        // when
        PageDetailResponseDTO result = pageService.getPageDetail(profileId, bookId, pageNum);

        // then
        assertNotNull(result);
        assertEquals(page.getId(), result.getPageId());
        assertEquals(page.getPageNumber(), result.getPageNumber());
        assertEquals(page.getImage(), result.getImage());
        assertEquals(page.getContent(), result.getContent());

        verify(profileRepository, times(1)).findById(profileId);
        verify(bookRepository, times(1)).findByIdAndProfile(bookId, profile);
        verify(pageRepository, times(1)).findPageWithUnknownWords(book, pageNum);
    }

    @Test
    @DisplayName("페이지 세부 정보 가져오기 단위 테스트 - 프로필이 존재하지 않을 때 예외 처리")
    void testGetPageDetail_ProfileNotFound() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;
        Integer pageNum = 1;

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> pageService.getPageDetail(profileId, bookId, pageNum));
    }

    @Test
    @DisplayName("페이지 세부 정보 가져오기 단위 테스트 - 책이 존재하지 않을 때 예외 처리")
    void testGetPageDetail_BookNotFound() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;
        Integer pageNum = 1;

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(bookId, profile)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> pageService.getPageDetail(profileId, bookId, pageNum));
    }

    @Test
    @DisplayName("페이지 세부 정보 가져오기 단위 테스트 - 페이지가 존재하지 않을 때 예외 처리")
    void testGetPageDetail_PageNotFound() {
        // given
        Integer profileId = 1;
        Integer bookId = 1;
        Integer pageNum = 1;

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(bookId, profile)).thenReturn(Optional.of(book));
        when(pageRepository.findPageWithUnknownWords(book, pageNum)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PageNotFoundException.class, () -> pageService.getPageDetail(profileId, bookId, pageNum));
    }
}
