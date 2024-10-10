package com.cojac.storyteller.test.integration.unknownWord;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.page.entity.PageEntity;
import com.cojac.storyteller.page.exception.PageNotFoundException;
import com.cojac.storyteller.page.repository.PageRepository;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.unknownWord.dto.UnknownWordDetailDTO;
import com.cojac.storyteller.unknownWord.dto.UnknownWordRequestDTO;
import com.cojac.storyteller.unknownWord.entity.UnknownWordEntity;
import com.cojac.storyteller.unknownWord.exception.UnknownWordNotFoundException;
import com.cojac.storyteller.unknownWord.repository.UnknownWordRepository;
import com.cojac.storyteller.unknownWord.service.UnknownWordService;
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
 * 통합 테스트
 *
 * 여러 구성 요소(서비스, 데이터베이스 등) 간의 상호작용을 검증하기 위한 테스트 클래스입니다.
 * 실제 데이터베이스와 리포지토리를 사용하며, 외부 서비스는 모의 객체로 처리하여
 * 외부 의존성을 최소화합니다. 이를 통해 실질적인 통합 상황에서의 기능과 동작을 검증합니다.
 */
@SpringBootTest
@Transactional
public class UnknownWordServiceIntegrationTest {

    @Autowired
    private UnknownWordService unknownWordService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private UnknownWordRepository unknownWordRepository;

    @Autowired
    private LocalUserRepository localUserRepository;

    private ProfileEntity profileEntity;
    private LocalUserEntity localUserEntity;
    private BookEntity bookEntity;
    private PageEntity pageEntity;

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
                .name("Test Name")
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
                .build();
        bookEntity = bookRepository.save(bookEntity);

        pageEntity = PageEntity.builder()
                .book(bookEntity)
                .pageNumber(1)
                .content("Sample content")
                .image("http://example.com/page/image.jpg")
                .build();
        pageEntity = pageRepository.save(pageEntity);
    }

    @Test
    @DisplayName("Unknown Word 저장 통합 테스트 - 성공")
    public void testSaveUnknownWord_Success() {
        // Given
        UnknownWordRequestDTO requestDTO = new UnknownWordRequestDTO();
        requestDTO.setProfileId(profileEntity.getId());
        requestDTO.setBookId(bookEntity.getId());
        requestDTO.setPageNum(pageEntity.getPageNumber());
        requestDTO.setUnknownWord("TestUnknown");
        requestDTO.setPosition(1);

        // When
        UnknownWordDetailDTO result = unknownWordService.saveUnknownWord(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("TestUnknown", result.getUnknownWord());
    }

    @Test
    @DisplayName("Unknown Word 저장 통합 테스트 - 프로필이 존재하지 않을 때 예외 처리")
    public void testSaveUnknownWord_ProfileNotFound() {
        // Given
        UnknownWordRequestDTO requestDTO = new UnknownWordRequestDTO();
        requestDTO.setProfileId(-1); // Invalid profile ID
        requestDTO.setBookId(bookEntity.getId());
        requestDTO.setPageNum(pageEntity.getPageNumber());
        requestDTO.setUnknownWord("TestUnknown");
        requestDTO.setPosition(1);

        // When & Then
        assertThrows(ProfileNotFoundException.class, () ->
                unknownWordService.saveUnknownWord(requestDTO));
    }

    @Test
    @DisplayName("Unknown Word 저장 통합 테스트 - 책이 존재하지 않을 때 예외 처리")
    public void testSaveUnknownWord_BookNotFound() {
        // Given
        UnknownWordRequestDTO requestDTO = new UnknownWordRequestDTO();
        requestDTO.setProfileId(profileEntity.getId());
        requestDTO.setBookId(-1);
        requestDTO.setPageNum(pageEntity.getPageNumber());
        requestDTO.setUnknownWord("TestUnknown");
        requestDTO.setPosition(1);

        // When & Then
        assertThrows(BookNotFoundException.class, () ->
                unknownWordService.saveUnknownWord(requestDTO));
    }

    @Test
    @DisplayName("Unknown Word 저장 통합 테스트 - 페이지가 존재하지 않을 때 예외 처리")
    public void testSaveUnknownWord_PageNotFound() {
        // Given
        UnknownWordRequestDTO requestDTO = new UnknownWordRequestDTO();
        requestDTO.setProfileId(profileEntity.getId());
        requestDTO.setBookId(bookEntity.getId());
        requestDTO.setPageNum(-1);
        requestDTO.setUnknownWord("TestUnknown");
        requestDTO.setPosition(1);

        // When & Then
        assertThrows(PageNotFoundException.class, () ->
                unknownWordService.saveUnknownWord(requestDTO));
    }

    @Test
    @DisplayName("Unknown Word 삭제 통합 테스트 - 성공")
    public void testDeleteUnknownWord_Success() {
        // Given
        UnknownWordEntity unknownWordEntity = new UnknownWordEntity("TestUnknown", 1, pageEntity);
        unknownWordEntity = unknownWordRepository.save(unknownWordEntity);
        final Integer unknownWordId = unknownWordEntity.getId();

        // When
        unknownWordService.deleteUnknownWord(unknownWordId);

        // Then
        assertThrows(UnknownWordNotFoundException.class, () ->
                unknownWordRepository.findById(unknownWordId).orElseThrow(
                        () -> new UnknownWordNotFoundException(ErrorCode.UNKNOWN_NOT_FOUND)));
    }

    @Test
    @DisplayName("Unknown Word 삭제 통합 테스트 - Unknown Word가 존재하지 않을 때 예외 처리")
    public void testDeleteUnknownWord_NotFound() {
        // When & Then
        assertThrows(UnknownWordNotFoundException.class, () ->
                unknownWordService.deleteUnknownWord(-1));
    }
}
