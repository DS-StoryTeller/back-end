package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer page;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false, length = 4000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    // BookEntity에서 사용해서 set메서드 하나만 만들었습니다.
    public void setBook(BookEntity book) {
        this.book = book;
    }
}