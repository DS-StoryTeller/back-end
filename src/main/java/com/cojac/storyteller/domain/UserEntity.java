package com.cojac.storyteller.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    private String role;

}
