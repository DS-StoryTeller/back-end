package com.cojac.storyteller.service.mapper;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.PageEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.SettingEntity;
import com.cojac.storyteller.dto.book.BookDTO;
import com.cojac.storyteller.dto.book.BookListResponseDTO;
import com.cojac.storyteller.dto.page.PageDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookMapper {
    public static BookEntity mapToBookEntity(String title, String content, String defaultCoverImage, ProfileEntity profile, SettingEntity setting) {
        BookEntity book = BookEntity.builder()
                .title(title)
                .coverImage(defaultCoverImage)
                .currentPage(0)
                .isReading(true)
                .isFavorite(false)
                .profile(profile)
                .setting(setting)
                .build();

        // \n\n 을 기준으로 동화 내용을 나눠 Page 객체를 추가
        String[] contentParts = content.split("\n\n");
        List<PageEntity> pages = IntStream.range(0, contentParts.length)
                .mapToObj(i -> PageEntity.builder()
                        .pageNumber(i + 1)
                        .content(contentParts[i].trim())
                        .image("defaultPageImage.jpg")
                        .book(book)
                        .build())
                .collect(Collectors.toList());

        pages.forEach(book::addPage);

        return book;
    }

    public static BookDTO mapToBookDTO(BookEntity book) {
        List<PageDTO> pageDTOs = book.getPages().stream()
                .map(page -> PageDTO.builder()
                        .id(page.getId())
                        .pageNumber(page.getPageNumber())
                        .image(page.getImage())
                        .content(page.getContent())
                        .bookId(page.getBook().getId())
                        .build())
                .collect(Collectors.toList());

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .coverImage(book.getCoverImage())
                .currentPage(book.getCurrentPage())
                .pages(pageDTOs)
                .isReading(book.isReading())
                .isFavorite(book.isFavorite())
                .totalPageCount(book.getPages().size())
                .profileId(book.getProfile().getId())
                .build();
    }

    public static List<BookListResponseDTO> mapToBookListResponseDTOs(List<BookEntity> books) {
        return books.stream()
                .map(book -> BookListResponseDTO.builder()
                        .bookId(book.getId())
                        .title(book.getTitle())
                        .coverImage(book.getCoverImage())
                        .currentPage(book.getCurrentPage())
                        .isReading(book.isReading())
                        .isFavorite(book.isFavorite())
                        .build())
                .collect(Collectors.toList());
    }
}
