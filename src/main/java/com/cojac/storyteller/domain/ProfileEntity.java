package com.cojac.storyteller.domain;

import com.cojac.storyteller.dto.profile.ProfileDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private LocalDate birthDate;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private String pinNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 책 목록 추가
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<BookEntity> books = new ArrayList<>();

    @Builder
    public ProfileEntity(String name, LocalDate birthDate, String imageUrl, String pinNumber, UserEntity user) {
        this.name = name;
        this.birthDate = birthDate;
        this.imageUrl = imageUrl;
        this.pinNumber = pinNumber;
        this.user = user;
    }

    public void addBook(BookEntity book) {
        books.add(book);
        book.setProfile(this);
    }

    public void updateProfile(ProfileDTO profileDTO) {
        this.name = profileDTO.getName();
        this.birthDate = profileDTO.getBirthDate();
        this.imageUrl = profileDTO.getImageUrl();
        this.pinNumber = profileDTO.getPinNumber();
    }
}