package com.gtel.srpingtutorial.redis.repository;

import com.gtel.srpingtutorial.redis.entities.OtpLimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpLimitRedisRepository extends JpaRepository<OtpLimitEntity, String> {
}
