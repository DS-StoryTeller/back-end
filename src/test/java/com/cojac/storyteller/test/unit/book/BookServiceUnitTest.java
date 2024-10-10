package com.cojac.storyteller.test.unit.book;

import com.cojac.storyteller.book.dto.BookDTO;
import com.cojac.storyteller.book.dto.BookDetailResponseDTO;
import com.cojac.storyteller.book.dto.BookListResponseDTO;
import com.cojac.storyteller.book.dto.QuizResponseDTO;
import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.book.repository.batch.BatchBookDelete;
import com.cojac.storyteller.book.service.BookService;
import com.cojac.storyteller.common.openAI.ImageGenerationService;
import com.cojac.storyteller.common.openAI.OpenAIService;
import com.cojac.storyteller.page.repository.batch.BatchPageInsert;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 단위 테스트
 *
 * 개별 메서드 및 클래스의 동작을 검증하기 위한 테스트 클래스입니다.
 * 각 테스트는 특정 기능이나 비즈니스 로직을 독립적으로 확인하며,
 * 외부 의존성을 최소화하기 위해 모의 객체를 사용합니다.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceUnitTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private OpenAIService openAIService;

    @Mock
    private ImageGenerationService imageGenerationService;

    @Mock
    private BatchPageInsert batchPageInsert;

    @Mock
    private BatchBookDelete batchBookDelete;

    private ProfileEntity profile;
    private BookEntity book;

    @BeforeEach
    void setUp() {
        profile = ProfileEntity.builder().id(1).birthDate(LocalDate.of(2015, 1, 1)).build();
        book = BookEntity.builder().id(1).profile(profile).title("Test Book").build();
    }

    /**
     * 동화 생성
     */
    @Test
    @DisplayName("동화 생성하기 단위 테스트 - 성공")
    void testCreateBook_Success() {
        // given
        String prompt = "Create a story";
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(openAIService.generateStory(any(), any())).thenReturn("Title: Test Book\nContent: This is a test story.");
        when(imageGenerationService.generateAndUploadBookCoverImage(any())).thenReturn("coverImageUrl");
        when(bookRepository.save(any())).thenReturn(book);
        doNothing().when(batchPageInsert).batchInsertPages(any());

        // when
        BookDTO result = bookService.createBook(prompt, profile.getId());

        // then
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(profileRepository, times(1)).findById(profile.getId());
        verify(openAIService, times(1)).generateStory(any(), any());
        verify(imageGenerationService, times(1)).generateAndUploadBookCoverImage(any());
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("동화 생성하기 단위 테스트 - 프로필 없음 예외")
    void testCreateBook_ProfileNotFound() {
        // given
        String prompt = "Create a story";
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> bookService.createBook(prompt, profile.getId()));
    }

    /**
     * 책 목록 조회
     */
    @Test
    @DisplayName("책 목록 페이지 조회하기 단위 테스트 - 성공")
    void testGetBooksPage_Success() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByProfile(eq(profile), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(book)));

        // when
        List<BookListResponseDTO> result = bookService.getBooksPage(profile.getId(), Pageable.unpaged());

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(profileRepository, times(1)).findById(profile.getId());
        verify(bookRepository, times(1)).findByProfile(eq(profile), any(Pageable.class));
    }

    @Test
    @DisplayName("책 목록 페이지 조회하기 단위 테스트 - 프로필 없음 예외")
    void testGetBooksPage_ProfileNotFound() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> bookService.getBooksPage(profile.getId(), any()));
    }

    /**
     * 즐겨찾기 책 목록 조회
     */
    @Test
    @DisplayName("즐겨찾기 책 조회하기 단위 테스트 - 성공")
    void testGetFavoriteBooks_Success() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByProfileAndIsFavoriteTrue(profile, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Collections.singletonList(book)));

        // when
        List<BookListResponseDTO> result = bookService.getFavoriteBooks(profile.getId(), Pageable.unpaged());

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(book.getTitle(), result.get(0).getTitle());
        verify(profileRepository, times(1)).findById(profile.getId());
        verify(bookRepository, times(1)).findByProfileAndIsFavoriteTrue(profile, Pageable.unpaged());
    }

    @Test
    @DisplayName("즐겨찾기 책 조회하기 단위 테스트 - 프로필 없음 예외")
    void testGetFavoriteBooks_ProfileNotFound() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> bookService.getFavoriteBooks(profile.getId(), any()));
    }

    /**
     * 읽고 있는 책 목록 조회
     */
    @Test
    @DisplayName("읽고 있는 책 조회하기 단위 테스트 - 성공")
    void testGetReadingBooks_Success() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByProfileAndIsReadingTrue(profile, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Collections.singletonList(book)));

        // when
        List<BookListResponseDTO> result = bookService.getReadingBooks(profile.getId(), Pageable.unpaged());

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(book.getTitle(), result.get(0).getTitle());
        verify(profileRepository, times(1)).findById(profile.getId());
        verify(bookRepository, times(1)).findByProfileAndIsReadingTrue(profile, Pageable.unpaged());
    }

    @Test
    @DisplayName("읽고 있는 책 조회하기 단위 테스트 - 프로필 없음 예외")
    void testGetReadingBook_ProfileNotFound() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> bookService.getReadingBooks(profile.getId(), any()));
    }

    /**
     * 책 세부 조회
     */
    @Test
    @DisplayName("책 상세 조회하기 단위 테스트 - 성공")
    void testGetBookDetail_Success() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.of(book));

        // when
        BookDetailResponseDTO result = bookService.getBookDetail(profile.getId(), book.getId());

        // then
        assertNotNull(result);
        assertEquals(book.getId(), result.getBookId());
        verify(profileRepository, times(1)).findById(profile.getId());
        verify(bookRepository, times(1)).findByIdAndProfile(book.getId(), profile);
    }

    @Test
    @DisplayName("책 상세 조회하기 단위 테스트 - 책 없음 예외")
    void testGetBookDetail_BookNotFound() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookService.getBookDetail(profile.getId(), book.getId()));
    }

    /**
     * 즐겨찾기 토글 기능 추가
     */
    @Test
    @DisplayName("즐겨찾기 상태 토글하기 단위 테스트 - 성공")
    void testToggleFavorite_Success() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.of(book));

        // when
        Boolean result = bookService.toggleFavorite(profile.getId(), book.getId());

        // then
        assertNotNull(result);
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("즐겨찾기 상태 토글하기 단위 테스트 - 책 없음 예외")
    void testToggleFavorite_BookNotFound() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookService.toggleFavorite(profile.getId(), book.getId()));
    }

    /**
     * 책 삭제 기능
     */
    @Test
    @DisplayName("책 삭제하기 단위 테스트 - 성공")
    void testDeleteBook_Success() throws Exception {
        // given
        when(profileRepository.existsById(profile.getId())).thenReturn(true);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        // when
        bookService.deleteBook(profile.getId(), book.getId());

        // then
        verify(batchBookDelete, times(1)).deleteByBookId(book.getId());
    }

    @Test
    @DisplayName("책 삭제하기 단위 테스트 - 프로필 없음 예외")
    void testDeleteBook_ProfileNotFound() {
        // given
        when(profileRepository.existsById(profile.getId())).thenReturn(false);

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> bookService.deleteBook(profile.getId(), book.getId()));
    }

    @Test
    @DisplayName("책 삭제하기 단위 테스트 - 책 없음 예외")
    void testDeleteBook_BookNotFound() {
        // given
        when(profileRepository.existsById(profile.getId())).thenReturn(true);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(profile.getId(), book.getId()));
    }

    /**
     * 현재 읽고 있는 페이지 업데이트
     */
    @Test
    @DisplayName("현재 페이지 업데이트하기 단위 테스트 - 성공")
    void testUpdateCurrentPage_Success() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));

        BookEntity book = mock(BookEntity.class);
        when(book.getTotalPageCount()).thenReturn(5);
        when(book.getProfile()).thenReturn(profile);

        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.of(book));

        // when
        BookDTO result = bookService.updateCurrentPage(profile.getId(), book.getId(), 1);

        // then
        assertNotNull(result);
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("현재 페이지 업데이트하기 단위 테스트 - 프로필 없음 예외")
    void testUpdateCurrentPage_ProfileNotFound() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> bookService.updateCurrentPage(profile.getId(), book.getId(), 1));
    }

    @Test
    @DisplayName("현재 페이지 업데이트하기 단위 테스트 - 책 없음 예외")
    void testUpdateCurrentPage_BookNotFound() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookService.updateCurrentPage(profile.getId(), book.getId(), 1));
    }

    /**
     * 퀴즈만 생성
     */
    @Test
    @DisplayName("퀴즈 생성하기 단위 테스트 - 성공")
    void testCreateQuiz_Success() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.of(book));
        when(openAIService.generateQuiz(any(), any())).thenReturn("Quiz Question");

        // when
        QuizResponseDTO result = bookService.createQuiz(profile.getId(), book.getId());

        // then
        assertNotNull(result);
        assertEquals("Quiz Question", result.getQuestion());
    }

    @Test
    @DisplayName("퀴즈 생성하기 단위 테스트 - 프로필 없음 예외")
    void testCreateQuiz_ProfileNotFound() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProfileNotFoundException.class, () -> bookService.createQuiz(profile.getId(), book.getId()));
    }

    @Test
    @DisplayName("퀴즈 생성하기 단위 테스트 - 책 없음 예외")
    void testCreateQuiz_BookNotFound() {
        // given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(bookRepository.findByIdAndProfile(book.getId(), profile)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookService.createQuiz(profile.getId(), book.getId()));
    }

}