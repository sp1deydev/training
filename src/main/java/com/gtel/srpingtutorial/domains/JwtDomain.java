package com.gtel.srpingtutorial.domains;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
@Slf4j
public class JwtDomain {

    @Value("${jwt.secretkey}")
    private String jwtSecret;


    @PostConstruct
    public void initData(){
        byte[] keyBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);

        Key key = Keys.hmacShaKeyFor(keyBytes);

        Jwts.builder().signWith(SignatureAlgorithm.HS512, key).compact();
    }
}
