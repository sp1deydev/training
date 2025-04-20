package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.entity.UserEntity;
import com.gtel.srpingtutorial.model.securities.UserInfoDetails;
import com.gtel.srpingtutorial.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserInfoService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByPhoneNumber(username);

        if (username == null){
            throw new UsernameNotFoundException("user " + username + " not found");
        }

        return new UserInfoDetails(userEntity);
    }
}
