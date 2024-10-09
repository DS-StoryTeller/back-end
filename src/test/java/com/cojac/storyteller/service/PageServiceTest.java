package com.cojac.storyteller.service;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.PageEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.page.PageDetailResponseDTO;
import com.cojac.storyteller.exception.BookNotFoundException;
import com.cojac.storyteller.exception.PageNotFoundException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.PageRepository;
import com.cojac.storyteller.repository.ProfileRepository;
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

@ExtendWith(MockitoExtension.class)
class PageServiceTest {

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
    @DisplayName("페이지 세부 정보 조회 - 성공")
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
    @DisplayName("페이지 세부 정보 조회 - 프로필 없음 예외")
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
    @DisplayName("페이지 세부 정보 조회 - 책 없음 예외")
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
    @DisplayName("페이지 세부 정보 조회 - 페이지 없음 예외")
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
