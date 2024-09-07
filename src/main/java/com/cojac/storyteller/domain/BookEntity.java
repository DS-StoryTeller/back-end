package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String coverImage;

    @Column(nullable = false)
    private Integer currentPage;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PageEntity> pages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @Column(nullable = false)
    private boolean isReading;

    @Column(nullable = false)
    private boolean isFavorite;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SettingEntity setting;


    public void addPage(PageEntity page) {
        pages.add(page);
        page.setBook(this);
    }

    public int getTotalPageCount() {
        return pages.size();
    }

    public void updateProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public void updateIsReading(boolean isReading) {
        this.isReading = isReading;
    }

    public void updateIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void updateCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public void updateCurrentPage(Integer currentPage) {
        if (currentPage < 0 || currentPage > getTotalPageCount()) {
            throw new IllegalArgumentException("Invalid page number");
        }
        this.currentPage = currentPage;
    }

}