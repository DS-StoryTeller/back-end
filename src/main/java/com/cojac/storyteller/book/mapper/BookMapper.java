package com.cojac.storyteller.book.mapper;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.page.entity.PageEntity;
import com.cojac.storyteller.profile.entity.ProfileEntity;
import com.cojac.storyteller.setting.entity.SettingEntity;
import com.cojac.storyteller.book.dto.BookDTO;
import com.cojac.storyteller.book.dto.BookListResponseDTO;
import com.cojac.storyteller.page.dto.PageDTO;

import java.util.List;
import java.util.stream.Collectors;


public class BookMapper {
    public static BookEntity mapToBookEntity(String title, String coverImageUrl, ProfileEntity profile, SettingEntity setting) {

        return BookEntity.builder()
                .title(title)
                .coverImage(coverImageUrl)
                .currentPage(0)
                .isReading(true)
                .isFavorite(false)
                .profile(profile)
                .setting(setting)
                .build();

    }

    public static BookDTO mapToBookDTO(BookEntity book, List<PageEntity> pages) {
        List<PageDTO> pageDTOs = pages.stream()
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
