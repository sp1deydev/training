package com.gtel.srpingtutorial.repository;

import com.gtel.srpingtutorial.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByPhoneNumber(String phoneNumber);

}
