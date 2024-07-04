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
import com.cojac.storyteller.repository.SettingRepository;
import com.cojac.storyteller.service.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;
    private final SettingRepository settingRepository;
    private final OpenAIService openAIService;

    /**
     * 동화와 퀴즈 생성
     */
    @Transactional
    public List<QuizResponseDTO>  createBook(String prompt, Integer profileId) {
        String defaultCoverImage = "defaultCover.jpg";

        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        Integer age = profile.getAge();
        String story = openAIService.generateStory(prompt, age);

        // 제목과 내용을 분리 (Title: 과 Content: 기준)
        String title = story.split("Content:")[0].replace("Title:", "").trim();
        String content = story.split("Content:")[1].trim();

        BookEntity book = BookMapper.mapToBookEntity(title, content, defaultCoverImage, profile);
        BookEntity savedBook = bookRepository.save(book);

        SettingEntity settingEntity = new SettingEntity(book);
        settingRepository.save(settingEntity);

        // 생성한 동화 내용으로 퀴즈 생성
        String quiz = openAIService.generateQuiz(story, age);

        // \n을 기준으로 퀴즈 분리
        List<String> questions = Arrays.asList(quiz.split("\n"));
        List<QuizResponseDTO> quizResponseDTOS = new ArrayList<>();

        questions.forEach(question ->  {
            // 괄호가 있는지 확인하고 제거
            int bracketIndex = question.indexOf('(');
            if (bracketIndex != -1) {
                question = question.substring(0, bracketIndex).trim();
            }
            quizResponseDTOS.add(new QuizResponseDTO(question));
        });

        return quizResponseDTOS;
    }

    public List<BookListResponseDTO> getAllBooks(Integer profileId) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        List<BookEntity> books = bookRepository.findByProfile(profile);
        return BookMapper.mapToBookListResponseDTOs(books);
    }

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

    public Boolean toggleFavorite(Integer profileId, Integer bookId) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        boolean newFavoriteStatus = !book.isFavorite();
        book.setFavorite(newFavoriteStatus);
        bookRepository.save(book);

        return newFavoriteStatus;
    }

    @Transactional
    public void deleteBook(Integer profileId, Integer bookId) throws ProfileNotFoundException, BookNotFoundException {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        bookRepository.delete(book);
    }
}