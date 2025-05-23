package com.gtel.srpingtutorial.domains.impl;


import com.gtel.srpingtutorial.domains.TokenDomain;
import com.gtel.srpingtutorial.exception.ApplicationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Component()
@Slf4j
public class JwtDomain implements TokenDomain {

    @Value("${jwt.secretkey}")
    private String jwtSecret;

    private Key key;

    @PostConstruct
    public void initData(){
        byte[] keyBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);

        key = Keys.hmacShaKeyFor(keyBytes);

//        Jwts.builder().signWith(SignatureAlgorithm.HS512, key).compact();
    }

    @Override
    public String genToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String validateToken(String token) throws ApplicationException {
        return this.extractUsername(token);
    }

    @Override
    public void extendTimeToLiveToken(String token, int timeToLivePlus) {

        // gen token new and return

    }

    @Override
    public void deleteAllTokenByUsername(String username) {

    }

    private String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
