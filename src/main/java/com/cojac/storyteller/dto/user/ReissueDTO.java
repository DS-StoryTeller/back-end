package com.cojac.storyteller.dto.user;

import com.cojac.storyteller.annotation.AtLeastOneNotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@AtLeastOneNotNull
public class ReissueDTO {

    private String username;
    private String accountId;
}
