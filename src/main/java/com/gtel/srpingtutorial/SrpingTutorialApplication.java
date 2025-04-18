package com.gtel.srpingtutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
//@EnableRedisRepositories(basePackages = "com.gtel.srpingtutorial.redis.repository")
public class SrpingTutorialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrpingTutorialApplication.class, args);
    }

}
