package com.cojac.storyteller.service;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.dto.book.BookDTO;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.service.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookDTO createBook(String title, String content) {
        String defaultCoverImage = "defaultCover.jpg";

        // BookMapper를 이용해 책 생성
        // 첫 생성시 페이지 0으로 설정
        BookEntity book = BookMapper.mapToBookEntity(title, content, defaultCoverImage, 0);
        // Book 저장 및 DTO 저장
        BookEntity savedBook = bookRepository.save(book);
        return BookMapper.mapToBookDTO(savedBook);
    }
}