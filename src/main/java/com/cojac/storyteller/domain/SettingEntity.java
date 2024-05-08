package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.cojac.storyteller.domain.FontSize.MEDIUM;
import static com.cojac.storyteller.domain.ReadingSpeed.ONE;

@Entity
@Getter
@NoArgsConstructor
public class SettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private FontSize fontSize;

    @Enumerated(EnumType.STRING)
    private ReadingSpeed readingSpeed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    public SettingEntity(BookEntity book) {
        this.book = book;
        this.fontSize = MEDIUM;
        this.readingSpeed = ONE;
    }
}
