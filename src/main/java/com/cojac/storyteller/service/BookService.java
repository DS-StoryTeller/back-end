package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.SettingEntity;
import com.cojac.storyteller.dto.book.BookDTO;
import com.cojac.storyteller.dto.book.BookDetailResponseDTO;
import com.cojac.storyteller.dto.book.BookListResponseDTO;
import com.cojac.storyteller.dto.page.PageDTO;
import com.cojac.storyteller.exception.BookNotFoundException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.ProfileRepository;
import com.cojac.storyteller.repository.SettingRepository;
import com.cojac.storyteller.service.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;
    private final SettingRepository settingRepository;
    private final OpenAIService openAIService;

    @Transactional
    public BookDTO createBook(String prompt, Integer profileId) {
        String defaultCoverImage = "defaultCover.jpg";

        // 프로필이 존재하는지 확인
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // OpenAI API를 호출하여 제목과 동화 생성
        String story = openAIService.generateStory(prompt);

        // 제목과 내용을 분리 (#### 기준)
        String[] storyParts = story.split("####");
        if (storyParts.length < 2) {
            throw new RuntimeException("Invalid response from OpenAI API");
        }

        String title = storyParts[0].trim();
        String content = storyParts[1].trim();

        // 매퍼 클래스를 사용해서 책 생성 및 저장
        BookEntity book = BookMapper.mapToBookEntity(title, content, defaultCoverImage, profile);
        BookEntity savedBook = bookRepository.save(book);

        // Book에 해당하는 SettingEntity 생성
        SettingEntity settingEntity = new SettingEntity(book);
        settingRepository.save(settingEntity);

        return BookMapper.mapToBookDTO(savedBook);
    }

    // 전체 동화 목록 가져오기
    public List<BookListResponseDTO> getAllBooks(Integer profileId) {
        // 프로필이 존재하는지 확인
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 프로필에 해당하는 동화 목록 가져오기
        List<BookEntity> books = bookRepository.findByProfile(profile);
        return BookMapper.mapToBookListResponseDTOs(books);
    }

    // 동화 상세 정보 가져오기
    public BookDetailResponseDTO getBookDetail(Integer profileId, Integer bookId) {
        // 프로필이 존재하는지 확인
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 해당 프로필에 해당하는 책 가져오기
        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 각 책에대한 페이지들을 가져와서 DTO로 변환
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

    // 즐겨찾기 변경시 사용 (토글)
    public Boolean toggleFavorite(Integer profileId, Integer bookId) {
        // 프로필이 존재하는지 확인
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 해당 프로필에 해당하는 책 가져오기
        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 즐겨찾기 상태 변경 후 저장
        boolean newFavoriteStatus = !book.isFavorite();
        book.setFavorite(newFavoriteStatus);
        bookRepository.save(book);

        return newFavoriteStatus;
    }

    // 동화 삭제
    @Transactional
    public void deleteBook(Integer profileId, Integer bookId) throws ProfileNotFoundException, BookNotFoundException {
        // 프로필이 존재하는지 확인
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 해당 프로필에 해당하는 책 가져오기
        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        bookRepository.delete(book);
    }
}