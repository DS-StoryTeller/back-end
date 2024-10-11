package com.cojac.storyteller.unit.unknownWord;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.page.entity.PageEntity;
import com.cojac.storyteller.page.exception.PageNotFoundException;
import com.cojac.storyteller.page.repository.PageRepository;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.unknownWord.dto.UnknownWordDetailDTO;
import com.cojac.storyteller.unknownWord.dto.UnknownWordRequestDTO;
import com.cojac.storyteller.unknownWord.entity.UnknownWordEntity;
import com.cojac.storyteller.unknownWord.exception.UnknownWordNotFoundException;
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
class UnknownWordServiceUnitTest {

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
    @DisplayName("Unknown Word 저장 단위 테스트 - 성공")
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
    @DisplayName("Unknown Word 저장 단위 테스트 - 프로필이 존재하지 않을 때 예외 처리")
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
    @DisplayName("Unknown Word 저장 단위 테스트 - 책이 존재하지 않을 때 예외 처리")
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
    @DisplayName("Unknown Word 저장 단위 테스트 - 페이지가 존재하지 않을 때 예외 처리")
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
    @DisplayName("Unknown Word 삭제 단위 테스트 - 성공")
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
    @DisplayName("Unknown Word 삭제 단위 테스트 - Unknown Word가 존재하지 않을 때 예외 처리")
    void testDeleteUnknownWord_NotFound() {
        // given
        Integer unknownWordId = 1;

        when(unknownWordRepository.findById(unknownWordId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UnknownWordNotFoundException.class, () -> unknownWordService.deleteUnknownWord(unknownWordId));
    }
}
