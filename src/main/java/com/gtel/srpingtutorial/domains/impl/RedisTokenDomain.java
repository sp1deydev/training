package com.gtel.srpingtutorial.domains.impl;

import com.gtel.srpingtutorial.domains.TokenDomain;
import com.gtel.srpingtutorial.exception.ApplicationException;
import com.gtel.srpingtutorial.redis.entities.LoginRedisEntity;
import com.gtel.srpingtutorial.redis.repository.LoginRedisRepository;
import com.gtel.srpingtutorial.utils.ERROR_CODE;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
@Slf4j
public class RedisTokenDomain implements TokenDomain {

    private final LoginRedisRepository loginRedisRepository;
    @Override
    public String genToken(String username) {

        String token = UUID.randomUUID().toString();


        LoginRedisEntity loginRedisEntity = new LoginRedisEntity();
        loginRedisEntity.setToken(token);
        loginRedisEntity.setUsername(username);
        loginRedisEntity.setTimeToLive(3600);


        loginRedisRepository.save(loginRedisEntity);
        return token;
    }

    @Override
    public String validateToken(String token) throws ApplicationException {

        Optional<LoginRedisEntity> opt = loginRedisRepository.findById(token);

         if (opt.isEmpty()){
            throw new ApplicationException(ERROR_CODE.USER_NOT_FOUND_OR_PASSWORD_NOT_MATCH);
        }

        LoginRedisEntity entity = opt.get();

        if (entity.getTimeToLive() < 30){
            entity.setTimeToLive(1000);

            loginRedisRepository.save(entity);
        }

        return entity.getUsername();
    }

    @Override
    public void extendTimeToLiveToken(String token, int timeToLivePlus) {
        Optional<LoginRedisEntity> opt = loginRedisRepository.findById(token);

        if (opt.isEmpty()){
            throw new ApplicationException(ERROR_CODE.USER_NOT_FOUND_OR_PASSWORD_NOT_MATCH);
        }

        LoginRedisEntity entity = opt.get();


        entity.setTimeToLive(entity.getTimeToLive()+ timeToLivePlus);

        loginRedisRepository.save(entity);
    }

    @Override
    public void deleteAllTokenByUsername(String username) {
        List<LoginRedisEntity> redisEntities = loginRedisRepository.findAllByUsername(username);

        if (!CollectionUtils.isEmpty(redisEntities))
            loginRedisRepository.deleteAll(redisEntities);
    }
}
