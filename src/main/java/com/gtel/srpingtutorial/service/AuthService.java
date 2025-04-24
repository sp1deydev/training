package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.domains.JwtDomain;
import com.gtel.srpingtutorial.entity.UserEntity;
import com.gtel.srpingtutorial.exception.ApplicationException;
import com.gtel.srpingtutorial.model.request.LoginRequest;
import com.gtel.srpingtutorial.model.response.LoginResponse;
import com.gtel.srpingtutorial.repository.UserRepository;
import com.gtel.srpingtutorial.utils.ERROR_CODE;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtDomain jwtDomain;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public LoginResponse login(LoginRequest request) throws ApplicationException {
        if (StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "Phone number or password is null");
        }

        log.info("[login] - login with phone number and password START");
        //use Optional
        UserEntity user = userRepository.findByPhoneNumber(request.getUsername());
        if (user == null) {
            log.info("[login] - login ERROR with user not found");
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.info("[login] - login ERROR with invalid credentials");
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "Invalid credentials");
        }

        String accessToken = jwtDomain.genJwt(user.getPhoneNumber());
        String refreshToken = jwtDomain.genRefreshToken(user.getPhoneNumber());
        log.info("[login] - login with phone number and password END");
        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "Refresh token is invalid");
        }

        log.info("[refreshToken] - refresh token with token {} START", refreshToken);

        String username = jwtDomain.extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            log.info("[refreshToken] - refresh token with token {} ERROR User Not Found", refreshToken);
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "User not found");
        }
        if(!jwtDomain.validateToken(refreshToken, userDetails)) {
            log.info("[refreshToken] - refresh token with token {} ERROR Invalid Token", refreshToken);
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "Refresh token is invalid");
        }
        String newAccessToken = jwtDomain.genJwt(username);
        String newRefreshToken = jwtDomain.genRefreshToken(username);
        log.info("[refreshToken] - refresh token with token {} START", refreshToken);
        return new LoginResponse(newAccessToken, newRefreshToken);
    }
}
