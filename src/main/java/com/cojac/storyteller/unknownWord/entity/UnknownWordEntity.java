package com.cojac.storyteller.unknownWord.entity;

import com.cojac.storyteller.page.entity.PageEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UnknownWordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String unknownWord;

    @Column(nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private PageEntity page;

    public UnknownWordEntity(String unknownWord, Integer position, PageEntity page) {
        this.unknownWord = unknownWord;
        this.position = position;
        this.page = page;
    }
}
