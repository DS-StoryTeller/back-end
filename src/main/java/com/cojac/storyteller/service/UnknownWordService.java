package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.PageEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.UnknownWordEntity;
import com.cojac.storyteller.dto.request.PageRequestDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDetailDto;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDto;
import com.cojac.storyteller.exception.BookNotFoundException;
import com.cojac.storyteller.exception.PageNotFoundException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.PageRepository;
import com.cojac.storyteller.repository.ProfileRepository;
import com.cojac.storyteller.repository.UnknownWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnknownWordService {
    private final PageRepository pageRepository;
    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;
    private final UnknownWordRepository unknownWordRepository;

    // 단어 저장
    public UnknownWordDetailDto saveUnknownWord(PageRequestDTO requestDto, UnknownWordDto unknownWordDto) {
        Integer profileId = requestDto.getProfileId();
        Integer bookId = requestDto.getBookId();
        Integer pageNum = requestDto.getPageNum();
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
        UnknownWordEntity unknownWordEntity = new UnknownWordEntity(unknownWordDto.getUnknownWord(), unknownWordDto.getPosition(), page);
        unknownWordRepository.save(unknownWordEntity);

        return UnknownWordDetailDto.builder()
                .bookId(bookId)
                .pageId(pageNum)
                .unknownwordId(unknownWordEntity.getId())
                .unknownWord(unknownWordDto.getUnknownWord())
                .position(unknownWordDto.getPosition())
                .build();
    }

    public void deleteUnknownWord(Integer unknownWordId) {
        // unknownword 가져오기
        UnknownWordEntity unknownWordEntity = unknownWordRepository.findById(unknownWordId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.UNKNOWN_NOT_FOUND));

        unknownWordRepository.delete(unknownWordEntity);
    }
}
