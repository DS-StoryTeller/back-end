package com.cojac.storyteller.page.entity;

import com.cojac.storyteller.book.entity.BookEntity;
import com.cojac.storyteller.unknownWord.entity.UnknownWordEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 엔티티명과 구분을 두고자 기존 page -> pageNumber로 변경
    @Column(nullable = false)
    private Integer pageNumber;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false, length = 4000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UnknownWordEntity> unknownWords = new ArrayList<>();

    // BookEntity에서 사용해서 set메서드 하나만 만들었습니다.
    public void setBook(BookEntity book) {
        this.book = book;
    }

    // 이미지 추가 삽입을 위한 set 메서드
    public void setImage(String image) {
        this.image = image;
    }
}