package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.PageEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.page.PageDetailResponseDTO;
import com.cojac.storyteller.exception.BookNotFoundException;
import com.cojac.storyteller.exception.PageNotFoundException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.PageRepository;
import com.cojac.storyteller.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;


    public PageDetailResponseDTO getPageDetail(Integer profileId, Integer bookId, Integer pageNum) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        PageEntity page = pageRepository.findByBookAndPageNumber(book, pageNum)
                .orElseThrow(() -> new PageNotFoundException(ErrorCode.PAGE_NOT_FOUND));

        return PageDetailResponseDTO.builder()
                .bookId(book.getId())
                .pageNumber(page.getPageNumber())
                .image(page.getImage())
                .content(page.getContent())
                .build();
    }
}
