package com.cojac.storyteller.domain;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refresh", timeToLive = 86400)  // 만료 시간 24시간
public class RefreshEntity {

    @Id
    private String refresh;

    private String username;

    public RefreshEntity(String refresh, String username) {
        this.username = username;
        this.refresh = refresh;
    }
}
