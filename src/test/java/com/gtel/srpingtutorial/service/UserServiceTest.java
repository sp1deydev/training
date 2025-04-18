package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.domains.OtpDomain;
import com.gtel.srpingtutorial.entity.UserEntity;
import com.gtel.srpingtutorial.exception.ApplicationException;
import com.gtel.srpingtutorial.model.request.ConfirmOtpRegisterRequest;
import com.gtel.srpingtutorial.model.request.RegisterRequest;
import com.gtel.srpingtutorial.model.response.RegisterResponse;
import com.gtel.srpingtutorial.redis.entities.OtpLimitEntity;
import com.gtel.srpingtutorial.redis.entities.RegisterUserEntity;
import com.gtel.srpingtutorial.redis.repository.OtpLimitRedisRepository;
import com.gtel.srpingtutorial.redis.repository.RegisterUserRedisRepository;
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

import java.util.Optional;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RegisterUserRedisRepository registerUserRedisRepository;

    @MockBean
    private OtpLimitRedisRepository otpLimitRedisRepository;


    // SEND OTP
    @Test
    void registerWithoutPhoneNumberAndPassword_failed() {
        String phoneNumber = "";
        String password = "";

        RegisterRequest request = new RegisterRequest(phoneNumber, password);

        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.registerUser(request)
        );

        assertEquals(ERROR_CODE.INVALID_PARAMETER.getErrorCode(), exception.getCode());
        assertEquals("phoneNumber is invalid", exception.getTitle());
    }
    @Test
    void registerWithoutPhoneNumber_failed() {
        String phoneNumber = "";
        String password = "abc";

        RegisterRequest request = new RegisterRequest(phoneNumber, password);

        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.registerUser(request)
        );

        assertEquals(ERROR_CODE.INVALID_PARAMETER.getErrorCode(), exception.getCode());
        assertEquals("phoneNumber is invalid", exception.getTitle());
    }
    @Test
    void registerWithoutPassword_failed() {
        String phoneNumber = "0382349132";
        String password = "";

        RegisterRequest request = new RegisterRequest(phoneNumber, password);

        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.registerUser(request)
        );

        assertEquals(ERROR_CODE.INVALID_PARAMETER.getErrorCode(), exception.getCode());
        assertEquals("password is invalid", exception.getTitle());
    }
    @Test
    void registerWithInvalidPhoneNumber_failed() {
        String phoneNumber = "9342312345";
        String password = "Qwertyu1@";

        RegisterRequest request = new RegisterRequest(phoneNumber, password);

        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.registerUser(request)
        );

        assertEquals(ERROR_CODE.INVALID_PARAMETER.getErrorCode(), exception.getCode());
        assertEquals("phoneNumber is invalid", exception.getTitle());
    }
    @Test
    void register_success() {
        String phoneNumber = "0382349133";
        String password = "Qwertyu1@";

        RegisterRequest request = new RegisterRequest(phoneNumber, password);

        OtpLimitEntity otpLimitEntity = new OtpLimitEntity();
        otpLimitEntity.setPhoneNumber(phoneNumber);
        otpLimitEntity.setDailyOtpCounter(0);
        when(otpLimitRedisRepository.findById(phoneNumber)).thenReturn(Optional.of(otpLimitEntity));

        RegisterResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertNotNull(response.getTransactionId());
    }
    @Test
    void resendOTPWithoutTransactionId_failed() {
        String transactionId = "";

        // WHEN + THEN
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.resendOTP(transactionId)
        );

        assertEquals("Transaction is not exists", exception.getTitle());
    }
    @Test
    void resendOTPWithInvalidTransactionId_failed() {
        String transactionId = "31fea99f-b3db-4dba-8f29-847b33994bb3";

        // WHEN + THEN
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.resendOTP(transactionId)
        );

        assertEquals("Transaction is not exists", exception.getTitle());
    }
    @Test
    void resendOTPBefore120s_failed() {
        String transactionId = "026162be-509a-46b8-b50d-1d17edf4c982";
        String phoneNumber = "0382349133";

        RegisterUserEntity entity = new RegisterUserEntity();
        entity.setTransactionId(transactionId);
        entity.setPhoneNumber(phoneNumber);
        entity.setOtpResendTime(System.currentTimeMillis() / 1000 + 120);

        when(registerUserRedisRepository.findById(transactionId))
                .thenReturn(Optional.of(entity));

        // WHEN + THEN
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.resendOTP(transactionId)
        );

        assertEquals("Resend OTP too early. Please wait.", exception.getTitle());
    }
    @Test
    void resendOtpAfter120s_success() {
        // GIVEN
        String transactionId = "026162be-509a-46b8-b50d-1d17edf4c982";

        RegisterUserEntity entity = new RegisterUserEntity();
        entity.setTransactionId(transactionId);
        entity.setPhoneNumber("0982573860");
        entity.setOtpResendTime(System.currentTimeMillis() / 1000 - 60);

        when(registerUserRedisRepository.findById(transactionId))
                .thenReturn(Optional.of(entity));

        RegisterResponse response = userService.resendOTP(transactionId);

        //THEN
        assertNotNull(response);
        assertNotNull(response.getTransactionId());
    }
    @Test
    void sendOtpMoreThan5Times_failed() {
        String transactionId = "026162be-509a-46b8-b50d-1d17edf4c982";

        RegisterUserEntity entity = new RegisterUserEntity();
        entity.setTransactionId(transactionId);
        entity.setPhoneNumber("84982573860");
        entity.setOtpResendTime(System.currentTimeMillis() / 1000 - 60);

        OtpLimitEntity otpLimitEntity = new OtpLimitEntity();
        otpLimitEntity.setPhoneNumber("84982573860");
        otpLimitEntity.setDailyOtpCounter(5);
        when(otpLimitRedisRepository.findById("84982573860")).thenReturn(Optional.of(otpLimitEntity));

        when(registerUserRedisRepository.findById(transactionId))
                .thenReturn(Optional.of(entity));

        // WHEN + THEN
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.resendOTP(transactionId)
        );
        assertEquals("OTP limit reached", exception.getTitle());
    }

    // CONFIRM OTP

}
