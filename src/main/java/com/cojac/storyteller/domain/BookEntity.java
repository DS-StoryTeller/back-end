package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.*;

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

    public void addPage(PageEntity page) {
        pages.add(page);
        page.setBook(this);
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }
}