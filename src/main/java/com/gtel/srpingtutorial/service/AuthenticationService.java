package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.domains.TokenDomain;
import com.gtel.srpingtutorial.domains.UserDomain;
import com.gtel.srpingtutorial.entity.RoleEntity;
import com.gtel.srpingtutorial.entity.UserEntity;
import com.gtel.srpingtutorial.exception.ApplicationException;
import com.gtel.srpingtutorial.model.request.LoginRequest;
import com.gtel.srpingtutorial.model.response.LoginResponse;
import com.gtel.srpingtutorial.utils.ERROR_CODE;
import com.gtel.srpingtutorial.utils.USER_STATUS;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AuthenticationService extends BaseService {

    private final UserDomain userDomain;

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;



    public void logoutAll(){

        String userName = getCurrentUserName();

        TokenDomain tokenDomain = domainFactory.tokenFactory();


        tokenDomain.deleteAllTokenByUsername(userName);


    }
    public LoginResponse login(LoginRequest request) throws ApplicationException {
        if (StringUtils.isBlank(request.getUsername())) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "Username không được để trống");
        }

        if (StringUtils.isBlank(request.getUsername())) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "Username không được để trống", HttpStatus.UNAUTHORIZED);
        }

        UserEntity userEntity = userDomain.getByUserName(request.getUsername());

        if (userEntity == null) {
            log.info("user {} not found on system", request.getUsername());

            throw new ApplicationException(ERROR_CODE.USER_NOT_FOUND_OR_PASSWORD_NOT_MATCH,
                    "Username không hợp lệ hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED);
        }

        if (userEntity.getStatus() != USER_STATUS.ACTIVE) {
            log.info("user {} is not active", request.getUsername());

            throw new ApplicationException(ERROR_CODE.USER_NOT_FOUND_OR_PASSWORD_NOT_MATCH,
                    "Username không hợp lệ hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED);
        }


        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new ApplicationException(ERROR_CODE.USER_NOT_FOUND_OR_PASSWORD_NOT_MATCH,
                    "Username không hợp lệ hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED);
        }


        TokenDomain tokenDomain = domainFactory.tokenFactory();
        List<String> roleNames = userEntity.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .toList();
        String token = tokenDomain.genToken(userEntity.getPhoneNumber(), roleNames);

        LoginResponse response  = new LoginResponse();

        response.setToken(token);
        return response;
    }
}
