package com.gtel.srpingtutorial.domains;

import com.gtel.srpingtutorial.domains.impl.JwtDomain;
import com.gtel.srpingtutorial.domains.impl.RedisTokenDomain;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DomainFactory {

    @Autowired
    private JwtDomain jwtDomain;

    @Autowired
    private RedisTokenDomain redisTokenDomain;

    @Value("${token.impl}")
    int tokenImpl;

    public TokenDomain tokenFactory(){
        if (tokenImpl == 1)
            return redisTokenDomain;

        return jwtDomain;
    }
}
