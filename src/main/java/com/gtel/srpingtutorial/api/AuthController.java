package com.gtel.srpingtutorial.api;

import com.gtel.srpingtutorial.model.request.LoginRequest;
import com.gtel.srpingtutorial.model.response.LoginResponse;
import com.gtel.srpingtutorial.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("refresh-token")
    public LoginResponse refreshToken(@RequestBody String refreshToken) {
        return authService.refreshToken(refreshToken);
    }
}
