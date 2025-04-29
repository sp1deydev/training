package com.gtel.srpingtutorial.domains;

import com.gtel.srpingtutorial.exception.ApplicationException;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.function.Function;

public interface TokenDomain {
    String genToken(String username, List<String> roles);

    String validateToken(String token) throws ApplicationException;

    void extendTimeToLiveToken(String token, int timeToLivePlus);

    void deleteAllTokenByUsername(String username);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

}
