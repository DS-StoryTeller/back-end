package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.PageEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.page.PageDetailResponseDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDTO;
import com.cojac.storyteller.exception.BookNotFoundException;
import com.cojac.storyteller.exception.PageNotFoundException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.PageRepository;
import com.cojac.storyteller.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;

    public PageDetailResponseDTO getPageDetail(Integer profileId, Integer bookId, Integer pageNum) {

        // 해당 프로필 가져오기
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 해당 프로필에 해당하는 책 가져오기
        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 해당 책에 해당하는 페이지 가져오기(모르는 단어와 같이)
        PageEntity page = pageRepository.findPageWithUnknownWords(book, pageNum)
                .orElseThrow(() -> new PageNotFoundException(ErrorCode.PAGE_NOT_FOUND));

        List<UnknownWordDTO> unknownWordDTOS = UnknownWordDTO.toDto(page.getUnknownWords());

        return PageDetailResponseDTO.builder()
                .pageId(page.getId())
                .pageNumber(page.getPageNumber())
                .image(page.getImage())
                .content(page.getContent())
                .unknownWords(unknownWordDTOS)
                .build();
    }

}
