package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.book.BookDTO;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.ProfileRepository;
import com.cojac.storyteller.service.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public BookDTO createBook(String title, String content, Integer profileId) {
        String defaultCoverImage = "defaultCover.jpg";

        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 매퍼 클래스를 사용해서 북 만들기
        BookEntity book = BookMapper.mapToBookEntity(title, content, defaultCoverImage, 0, profile);
        BookEntity savedBook = bookRepository.save(book);
        return BookMapper.mapToBookDTO(savedBook);
    }
}