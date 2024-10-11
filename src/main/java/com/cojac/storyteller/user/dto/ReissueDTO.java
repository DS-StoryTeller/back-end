package com.cojac.storyteller.user.dto;

import com.cojac.storyteller.user.annotation.AtLeastOneNotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@AtLeastOneNotNull
public class ReissueDTO {

    private String username;
    private String accountId;
}
