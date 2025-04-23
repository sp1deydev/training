package com.gtel.srpingtutorial.redis.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@Data
@RedisHash("login")
public class LoginRedisEntity {

    @Id
    private String token;

    @Indexed
    private String username;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private long timeToLive;
}
