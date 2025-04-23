package com.gtel.srpingtutorial.api;

import com.gtel.srpingtutorial.exception.ApplicationException;
import com.gtel.srpingtutorial.model.request.LoginRequest;
import com.gtel.srpingtutorial.model.response.LoginResponse;
import com.gtel.srpingtutorial.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class LoginControllers {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) throws ApplicationException {
        return authenticationService.login(request);
    }

    @PostMapping("/logout-all")
    public void logout()  {
        authenticationService.logoutAll();
    }
}
