package com.gtel.srpingtutorial.redis.repository;

import com.gtel.srpingtutorial.redis.entities.LoginRedisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginRedisRepository extends JpaRepository<LoginRedisEntity, String> {

    List<LoginRedisEntity> findAllByUsername(String userName);
}
