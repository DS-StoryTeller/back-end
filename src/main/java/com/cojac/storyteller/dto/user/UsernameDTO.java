package com.cojac.storyteller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsernameDTO {

    private String username;
    private boolean authResult;
}
