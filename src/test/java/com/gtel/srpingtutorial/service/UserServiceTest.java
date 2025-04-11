package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.domains.OtpDomain;
import com.gtel.srpingtutorial.entity.UserEntity;
import com.gtel.srpingtutorial.exception.ApplicationException;
import com.gtel.srpingtutorial.model.request.RegisterRequest;
import com.gtel.srpingtutorial.repository.UserRepository;
import com.gtel.srpingtutorial.utils.ERROR_CODE;
import com.gtel.srpingtutorial.utils.PhoneNumberUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
//@TestPropertySource("/test.properties")
@AutoConfigureMockMvc
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "038383", "+849999", "849825738", "849825738555555", "1234567890"})
    void checkInvalidPhoneNumber(String phoneNumber) {
        //GIVEN
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhoneNumber(phoneNumber);
        //WHEN
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                PhoneNumberUtils.validatePhoneNumber(registerRequest.getPhoneNumber())
        );
        //THEN
        assertEquals(ERROR_CODE.INVALID_PARAMETER.getErrorCode(), exception.getCode());
        assertEquals("phoneNumber is invalid", exception.getTitle());
    }
    @ParameterizedTest
    @ValueSource(strings = {"84982573860", "+84982573860", "0982573860"})
    void checkValidPhoneNumber(String phoneNumber) {
        //GIVEN
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhoneNumber(phoneNumber);
        //WHEN
        String validated = PhoneNumberUtils.validatePhoneNumber(registerRequest.getPhoneNumber());
        //THEN
        assertTrue(validated.startsWith("84"));
        assertEquals(11, validated.length());
    }

}
