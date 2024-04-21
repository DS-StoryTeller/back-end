package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Integer age;

    @Column(nullable = true)
    private String image;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public ProfileEntity(String name, Integer age, String image, UserEntity user) {
        this.name = name;
        this.age = age;
        this.image = image;
        this.user = user;
    }

}
