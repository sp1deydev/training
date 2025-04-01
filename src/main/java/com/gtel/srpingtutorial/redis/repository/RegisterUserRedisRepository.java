package com.gtel.srpingtutorial.redis.repository;

import com.gtel.srpingtutorial.redis.entities.RegisterUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterUserRedisRepository extends JpaRepository<RegisterUserEntity, String> {
}
