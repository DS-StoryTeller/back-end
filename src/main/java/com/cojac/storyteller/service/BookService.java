package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.PageEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.SettingEntity;
import com.cojac.storyteller.dto.book.BookDTO;
import com.cojac.storyteller.dto.book.BookDetailResponseDTO;
import com.cojac.storyteller.dto.book.BookListResponseDTO;
import com.cojac.storyteller.dto.book.QuizResponseDTO;
import com.cojac.storyteller.dto.page.PageDTO;
import com.cojac.storyteller.exception.BookNotFoundException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.ProfileRepository;
import com.cojac.storyteller.service.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;
    private final OpenAIService openAIService;
    private final ImageGenerationService imageGenerationService;

    /**
     * 동화와 퀴즈 생성
     */
    @Transactional
    public BookDTO createBook(String prompt, Integer profileId) {
        String defaultCoverImage = "defaultCover.jpg";

        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // birthDate를 이용하여 age를 계산하는 코드를 추가했습니다.
        LocalDate birthDate = profile.getBirthDate();
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();

        String story = openAIService.generateStory(prompt, age);

        // 제목과 내용을 분리 (Title: 과 Content: 기준)
        String title = story.split("Content:")[0].replace("Title:", "").trim();
        String content = story.split("Content:")[1].trim();

        // Setting 초기 설정
        SettingEntity setting  = SettingEntity.createDefaultSetting();

        BookEntity book = BookMapper.mapToBookEntity(title, content, defaultCoverImage, profile, setting);
        BookEntity savedBook = bookRepository.save(book);

        // 책 표지 이미지 생성 및 업로드
        String coverImageUrl = imageGenerationService.generateAndUploadBookCoverImage(title);
        savedBook.updateCoverImage(coverImageUrl);
        bookRepository.save(savedBook);

        return BookMapper.mapToBookDTO(savedBook);
    }

    /**
     * 책 목록 조회
     */
    public List<BookListResponseDTO> getBooksPage(Integer profileId, Pageable pageable) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        Page<BookEntity> booksPage = bookRepository.findByProfile(profile, pageable);
        return BookMapper.mapToBookListResponseDTOs(booksPage.getContent());
    }

    /**
     * 즐겨찾기 책 목록 조회
     */
    public List<BookListResponseDTO> getFavoriteBooks(Integer profileId, Pageable pageable) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        Page<BookEntity> books = bookRepository.findByProfileAndIsFavoriteTrue(profile, pageable);
        return BookMapper.mapToBookListResponseDTOs(books.getContent());
    }

    /**
     * 읽고 있는 책 목록 조회
     */
    public List<BookListResponseDTO> getReadingBooks(Integer profileId, Pageable pageable) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        Page<BookEntity> books = bookRepository.findByProfileAndIsReadingTrue(profile, pageable);
        return BookMapper.mapToBookListResponseDTOs(books.getContent());
    }

    /**
     * 책 세부 조회
     */
    public BookDetailResponseDTO getBookDetail(Integer profileId, Integer bookId) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        List<PageDTO> pageDTOs = book.getPages().stream()
                .map(page -> PageDTO.builder()
                        .id(page.getId())
                        .pageNumber(page.getPageNumber())
                        .image(page.getImage())
                        .content(page.getContent())
                        .bookId(page.getBook().getId())
                        .build())
                .collect(Collectors.toList());

        return BookDetailResponseDTO.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .coverImage(book.getCoverImage())
                .currentPage(book.getCurrentPage())
                .totalPageCount(book.getPages().size())
                .pages(pageDTOs)
                .build();
    }

    /**
     * 즐겨찾기 토글 기능 추가
     */
    public Boolean toggleFavorite(Integer profileId, Integer bookId) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        boolean newFavoriteStatus = !book.isFavorite();
        book.updateIsFavorite(newFavoriteStatus);
        bookRepository.save(book);

        return newFavoriteStatus;
    }

    /**
     * 책 삭제 기능 추가
     */

    @Transactional
    public void deleteBook(Integer profileId, Integer bookId) throws ProfileNotFoundException, BookNotFoundException {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        bookRepository.delete(book);
    }

    /**
     * 현재 읽고 있는 페이지 업데이트
     */
    @Transactional
    public BookDTO updateCurrentPage(Integer profileId, Integer bookId, Integer currentPage) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        book.updateCurrentPage(currentPage);
        if (currentPage >= book.getTotalPageCount()) {
            book.updateIsReading(false);
        } else {
            book.updateIsReading(true);
        }
        bookRepository.save(book);

        return BookMapper.mapToBookDTO(book);
    }

    /**
     * 퀴즈만 생성
     */
    public QuizResponseDTO createQuiz(Integer profileId, Integer bookId) {
        String defaultCoverImage = "defaultCover.jpg";

        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 책 내용 story 변수에 담기
        StringBuilder story = new StringBuilder();
        for (PageEntity page : book.getPages()) {
            story.append(page.getContent());
            story.append("\n\n");
        }

        // birthDate로 age 얻기
        LocalDate birthDate = profile.getBirthDate();
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();

        // 생성한 동화 내용으로 퀴즈 생성
        String quiz = openAIService.generateQuiz(story.toString(), age);

        return new QuizResponseDTO(quiz);
    }
}
