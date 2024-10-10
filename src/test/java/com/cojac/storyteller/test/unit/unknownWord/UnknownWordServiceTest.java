package com.cojac.storyteller.test.unit.unknownWord;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.page.entity.PageEntity;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.unknownWord.entity.UnknownWordEntity;
import com.cojac.storyteller.unknownWord.dto.UnknownWordDetailDTO;
import com.cojac.storyteller.unknownWord.dto.UnknownWordRequestDTO;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.page.exception.PageNotFoundException;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.unknownWord.exception.UnknownWordNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.page.repository.PageRepository;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.unknownWord.repository.UnknownWordRepository;
import com.cojac.storyteller.unknownWord.service.UnknownWordService;
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
class UnknownWordServiceTest {

    @Mock
    private PageRepository pageRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UnknownWordRepository unknownWordRepository;

    @InjectMocks
    private UnknownWordService unknownWordService;

    private ProfileEntity profile;
    private BookEntity book;
    private PageEntity page;
    private UnknownWordEntity unknownWord;

    @BeforeEach
    void setUp() {
        profile = ProfileEntity.builder().id(1).build();
        book = BookEntity.builder().id(1).profile(profile).title("Test Book").build();
        page = PageEntity.builder().id(1).pageNumber(1).build();
        unknownWord = new UnknownWordEntity("testWord", 1, page);
    }

    /**
     * 단어 저장
     */
    @Test
    @DisplayName("단어 저장 - 성공")
    void testSaveUnknownWord_Success() {
        // given
        UnknownWordRequestDTO unknownWordRequestDTO = new UnknownWordRequestDTO();
        unknownWordRequestDTO.setProfileId(profile.getId());
        unknownWordRequestDTO.setBookId(book.getId());
        unknownWordRequestDTO.setPageNum(page.getPageNumber());
        unknownWordRequestDTO.setUnknownWord("testWord");
        unknownWordRequestDTO.setPosition(1);

        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.of(book));
        when(pageRepository.findByBookAndPageNumber(book, page.getPageNumber())).thenReturn(Optional.of(page));
        when(unknownWordRepository.save(any(UnknownWordEntity.class))).thenReturn(unknownWord);

        // when
        UnknownWordDetailDTO result = unknownWordService.saveUnknownWord(unknownWordRequestDTO);

        // then
        assertNotNull(result);
        assertEquals(book.getId(), result.getBookId());
        assertEquals(page.getPageNumber(), result.getPageId());
        assertEquals(unknownWord.getId(), result.getUnknownwordId());
        assertEquals(unknownWordRequestDTO.getUnknownWord(), result.getUnknownWord());
        assertEquals(unknownWordRequestDTO.getPosition(), result.getPosition());

        verify(profileRepository, times(1)).findById(profile.getId());
        verify(bookRepository, times(1)).findByIdAndProfile(book.getId(), profile);
        verify(pageRepository, times(1)).findByBookAndPageNumber(book, page.getPageNumber());
        verify(unknownWordRepository, times(1)).save(any(UnknownWordEntity.class));
    }

    @Test
    @DisplayName("단어 저장 - 프로필 없음 예외")
    void testSaveUnknownWord_ProfileNotFound() {
        // given
        UnknownWordRequestDTO unknownWordRequestDTO = new UnknownWordRequestDTO();
        unknownWordRequestDTO.setProfileId(999); // 존재하지 않는 프로필 ID
        unknownWordRequestDTO.setBookId(book.getId());
        unknownWordRequestDTO.setPageNum(page.getPageNumber());

        when(profileRepository.findById(unknownWordRequestDTO.getProfileId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> unknownWordService.saveUnknownWord(unknownWordRequestDTO));
    }

    @Test
    @DisplayName("단어 저장 - 책 없음 예외")
    void testSaveUnknownWord_BookNotFound() {
        // given
        UnknownWordRequestDTO unknownWordRequestDTO = new UnknownWordRequestDTO();
        unknownWordRequestDTO.setProfileId(profile.getId());
        unknownWordRequestDTO.setBookId(999); // 존재하지 않는 책 ID
        unknownWordRequestDTO.setPageNum(page.getPageNumber());

        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(unknownWordRequestDTO.getBookId(), profile)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> unknownWordService.saveUnknownWord(unknownWordRequestDTO));
    }

    @Test
    @DisplayName("단어 저장 - 페이지 없음 예외")
    void testSaveUnknownWord_PageNotFound() {
        // given
        UnknownWordRequestDTO unknownWordRequestDTO = new UnknownWordRequestDTO();
        unknownWordRequestDTO.setProfileId(profile.getId());
        unknownWordRequestDTO.setBookId(book.getId());
        unknownWordRequestDTO.setPageNum(999); // 존재하지 않는 페이지 번호

        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.of(book));
        when(pageRepository.findByBookAndPageNumber(book, unknownWordRequestDTO.getPageNum())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PageNotFoundException.class, () -> unknownWordService.saveUnknownWord(unknownWordRequestDTO));
    }

    /**
     * 단어 삭제
     */
    @Test
    @DisplayName("단어 삭제 - 성공")
    void testDeleteUnknownWord_Success() {
        // given
        Integer unknownWordId = 1;

        when(unknownWordRepository.findById(unknownWordId)).thenReturn(Optional.of(unknownWord));

        // when
        unknownWordService.deleteUnknownWord(unknownWordId);

        // then
        verify(unknownWordRepository, times(1)).findById(unknownWordId);
        verify(unknownWordRepository, times(1)).delete(unknownWord);
    }

    @Test
    @DisplayName("단어 삭제 - 단어 없음 예외")
    void testDeleteUnknownWord_NotFound() {
        // given
        Integer unknownWordId = 1;

        when(unknownWordRepository.findById(unknownWordId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UnknownWordNotFoundException.class, () -> unknownWordService.deleteUnknownWord(unknownWordId));
    }
}
