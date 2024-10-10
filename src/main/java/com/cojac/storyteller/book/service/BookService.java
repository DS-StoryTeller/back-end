package com.cojac.storyteller.book.service;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.book.repository.batch.BatchBookDelete;
import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.page.entity.PageEntity;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.setting.entity.SettingEntity;
import com.cojac.storyteller.book.dto.BookDTO;
import com.cojac.storyteller.book.dto.BookDetailResponseDTO;
import com.cojac.storyteller.book.dto.BookListResponseDTO;
import com.cojac.storyteller.book.dto.QuizResponseDTO;
import com.cojac.storyteller.page.dto.PageDTO;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.page.repository.batch.BatchPageInsert;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.common.amazon.AmazonS3Service;
import com.cojac.storyteller.common.openAI.ImageGenerationService;
import com.cojac.storyteller.common.openAI.OpenAIService;
import com.cojac.storyteller.book.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;
    private final OpenAIService openAIService;
    private final ImageGenerationService imageGenerationService;
    private final BatchPageInsert batchPageInsert;
    private final BatchBookDelete batchBookDelete;
    private final AmazonS3Service amazonS3Service;

    // 동화 생성 중인지 확인하는 맵 (프로필 ID를 키로 사용)
    private final ConcurrentHashMap<Integer, Boolean> creatingBooks = new ConcurrentHashMap<>();

    /**
     * 동화 생성
     */
    @Transactional
    public BookDTO createBook(String prompt, Integer profileId) {
        // 동화 생성 중복 확인
        if (creatingBooks.getOrDefault(profileId, false)) {
            throw new IllegalStateException("이미 동화가 생성 중입니다. 나중에 다시 시도해주세요.");
        }

        // 동화 생성 중 상태로 설정
        creatingBooks.put(profileId, true);

        try {
            // 프로필 확인
            ProfileEntity profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

            // 나이를 계산 (birthDate 기준)
            LocalDate birthDate = profile.getBirthDate();
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();

            // OpenAI 서비스로부터 동화 생성
            String story = openAIService.generateStory(prompt, age);

            // 제목과 내용을 분리 (Title: 과 Content: 기준)
            String title = story.split("Content:")[0].replace("Title:", "").trim();
            String content = story.split("Content:")[1].trim();

            // Setting 초기 설정
            SettingEntity setting = SettingEntity.createDefaultSetting();

            // 책 표지 이미지 생성 및 업로드
            String coverImageUrl = imageGenerationService.generateAndUploadBookCoverImage(title);

            // 책 엔티티 생성
            BookEntity book = BookMapper.mapToBookEntity(title, coverImageUrl, profile, setting);
            BookEntity savedBook = bookRepository.save(book);

            // 페이지 생성
            List<PageEntity> pages = createPage(savedBook, content);
            batchPageInsert.batchInsertPages(pages);

            // 성공적으로 생성된 동화 반환
            return BookMapper.mapToBookDTO(savedBook, pages);

        } finally {
            // 동화 생성이 끝나면 상태를 제거하여 다시 요청 가능하게 함
            creatingBooks.remove(profileId);
        }
    }

    private List<PageEntity> createPage(BookEntity book, String content) {
        String[] contentParts = content.split("\n\n");
        List<PageEntity> pages = new ArrayList<>();

        for (int i = 0; i < contentParts.length; i++) {
            String trimContent = contentParts[i].trim();

            String imageUrl = imageGenerationService.generateAndUploadPageImage(trimContent);

            PageEntity pageEntity = PageEntity.builder()
                    .pageNumber(i + 1)
                    .content(trimContent)
                    .image(imageUrl)
                    .book(book)
                    .build();
            pages.add(pageEntity);
        }

        return pages;
    }

    /**
     * 책 목록 조회
     */
    @Cacheable(value = "bookListCache", key = "#profileId", unless = "#result.isEmpty()")
    public List<BookListResponseDTO> getBooksPage(Integer profileId, Pageable pageable) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        Page<BookEntity> booksPage = bookRepository.findByProfile(profile, pageable);
        return BookMapper.mapToBookListResponseDTOs(booksPage.getContent());
    }

    /**
     * 즐겨찾기 책 목록 조회
     */
    @Cacheable(value = "favoriteBooksCache", key = "#profileId", unless = "#result.isEmpty()")
    public List<BookListResponseDTO> getFavoriteBooks(Integer profileId, Pageable pageable) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        Page<BookEntity> books = bookRepository.findByProfileAndIsFavoriteTrue(profile, pageable);
        return BookMapper.mapToBookListResponseDTOs(books.getContent());
    }

    /**
     * 읽고 있는 책 목록 조회
     */
    @Cacheable(value = "readingBooksCache", key = "#profileId", unless = "#result.isEmpty()")
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
    @CacheEvict(value = {"bookListCache", "favoriteBooksCache"}, key = "#profileId")
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
     * 책 삭제 기능
     */
    @Transactional
    @CacheEvict(value = {"bookListCache", "favoriteBooksCache", "readingBooksCache"}, allEntries = true)
    public void deleteBook(Integer profileId, Integer bookId) throws Exception {

        if (!profileRepository.existsById(profileId)) {
            throw new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND);
        }

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 책과 페이지 이미지 삭제
        deleteBookAndPageImages(book);

        batchBookDelete.deleteByBookId(bookId);
    }

    private void deleteBookAndPageImages(BookEntity book) throws Exception {
        // S3에서 책 표지 이미지 삭제
        deleteImageIfNotNull(book.getCoverImage());

        // 각 페이지의 이미지 삭제
        for (PageEntity page : book.getPages()) {
            if (page.getImage() != null) {
                amazonS3Service.deleteS3(page.getImage());
            }
        }
    }

    private void deleteImageIfNotNull(String imageUrl) throws Exception {
        if (imageUrl != null) {
            amazonS3Service.deleteS3(imageUrl);
        }
    }

    /**
     * 현재 읽고 있는 페이지 업데이트
     */
    @Transactional
    @CacheEvict(value = {"bookListCache", "readingBooksCache"}, key = "#profileId")
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
