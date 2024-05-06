package com.cojac.storyteller.service.mapper;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.PageEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.book.BookDTO;
import com.cojac.storyteller.dto.page.PageDTO;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookMapper {
    public static BookEntity mapToBookEntity(String title, String content, String defaultCoverImage, Integer currentPage, ProfileEntity profile) {
        BookEntity book = BookEntity.builder()
                .title(title)
                .coverImage(defaultCoverImage)
                .currentPage(0)  // 처음 책 생성 시 0페이지로 설정
                .isReading(true) // 책 생성후 바로 보인다 가정하여 true
                .isFavorite(false)
                .build();

        // OpenAI 연결 전 #### 을 기준으로 동화 내용이 들어온다 가정하고 나눴습니다.
        // ####을 기준으로 동화 내용을 잘라 Page 객체를 넣음
        List<PageEntity> pages = IntStream.range(0, content.split("####").length)
                .mapToObj(i -> PageEntity.builder()
                        .page(i + 1)
                        .content(content.split("####")[i])
                        .image("defaultPageImage.jpg") // 이미지 역시 기본 이미지를 넣고, 추후 변경하는 것으로 하겠습니다.
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
                        .page(page.getPage())
                        .image(page.getImage())
                        .content(page.getContent())
                        .build())
                .collect(Collectors.toList());

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .coverImage(book.getCoverImage())
                .currentPage(book.getCurrentPage())
//                .pages(pageDTOs) page 내용 필요하면 주석 제거
                .pages(null)
                .isReading(book.isReading())
                .isFavorite(book.isFavorite())
                .totalPageCount(book.getTotalPageCount())
                .build();
    }
}