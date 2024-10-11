package com.cojac.storyteller.unknownWord.service;

import com.cojac.storyteller.response.code.ErrorCode;
import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.page.entity.PageEntity;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.unknownWord.dto.UnknownWordDetailDTO;
import com.cojac.storyteller.unknownWord.dto.UnknownWordRequestDTO;
import com.cojac.storyteller.book.exception.BookNotFoundException;
import com.cojac.storyteller.page.exception.PageNotFoundException;
import com.cojac.storyteller.profile.exception.ProfileNotFoundException;
import com.cojac.storyteller.book.repository.BookRepository;
import com.cojac.storyteller.page.repository.PageRepository;
import com.cojac.storyteller.profile.repository.ProfileRepository;
import com.cojac.storyteller.unknownWord.entity.UnknownWordEntity;
import com.cojac.storyteller.unknownWord.exception.UnknownWordNotFoundException;
import com.cojac.storyteller.unknownWord.repository.UnknownWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnknownWordService {
    private final PageRepository pageRepository;
    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;
    private final UnknownWordRepository unknownWordRepository;

    /**
     * 단어 저장
     */
    public UnknownWordDetailDTO saveUnknownWord(UnknownWordRequestDTO unknownWordRequestDTO) {
        Integer profileId = unknownWordRequestDTO.getProfileId();
        Integer bookId = unknownWordRequestDTO.getBookId();
        Integer pageNum = unknownWordRequestDTO.getPageNum();

        // 해당 프로필 가져오기
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 해당 프로필에 해당하는 책 가져오기
        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 해당 책에 해당하는 페이지 가져오기
        PageEntity page = pageRepository.findByBookAndPageNumber(book, pageNum)
                .orElseThrow(() -> new PageNotFoundException(ErrorCode.PAGE_NOT_FOUND));

        // UnknownWord 저장
        UnknownWordEntity unknownWordEntity = new UnknownWordEntity(unknownWordRequestDTO.getUnknownWord(), unknownWordRequestDTO.getPosition(), page);
        unknownWordRepository.save(unknownWordEntity);

        return UnknownWordDetailDTO.builder()
                .bookId(bookId)
                .pageId(pageNum)
                .unknownwordId(unknownWordEntity.getId())
                .unknownWord(unknownWordRequestDTO.getUnknownWord())
                .position(unknownWordRequestDTO.getPosition())
                .build();
    }

    /**
     * 단어 삭제
     */
    public void deleteUnknownWord(Integer unknownWordId) {
        // unknownword 가져오기
        UnknownWordEntity unknownWordEntity = unknownWordRepository.findById(unknownWordId)
                .orElseThrow(() -> new UnknownWordNotFoundException(ErrorCode.UNKNOWN_NOT_FOUND));

        unknownWordRepository.delete(unknownWordEntity);
    }
}
