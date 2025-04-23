package com.gtel.srpingtutorial.domains;

import com.gtel.srpingtutorial.exception.ApplicationException;

public interface TokenDomain {
    String genToken(String username);

    String validateToken(String token) throws ApplicationException;

    void extendTimeToLiveToken(String token, int timeToLivePlus);

    void deleteAllTokenByUsername(String username);
}
