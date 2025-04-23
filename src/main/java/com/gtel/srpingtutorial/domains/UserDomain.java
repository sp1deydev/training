package com.gtel.srpingtutorial.domains;

import com.gtel.srpingtutorial.entity.UserEntity;
import com.gtel.srpingtutorial.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class UserDomain {
    private final UserRepository userRepository;


    public UserEntity getByUserName(String username){
        return userRepository.findByPhoneNumber(username);
    }
}
