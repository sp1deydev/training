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

    //PHONE NUMBER UNIT TEST
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "038383", "+849999", "849825738", "849825738555555", "1234567890"})
    void checkInvalidPhoneNumber_failed(String phoneNumber) {
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
    void checkValidPhoneNumber_success(String phoneNumber) {
        //GIVEN
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhoneNumber(phoneNumber);
        //WHEN
        String validated = PhoneNumberUtils.validatePhoneNumber(registerRequest.getPhoneNumber());
        //THEN
        assertTrue(validated.startsWith("84"));
        assertEquals(11, validated.length());
    }


    //PASSWORD UNIT TEST
    @Test
    void checkNullPassword_failed() {
        //GIVEN
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0982573860");
        request.setPassword("");
        //WHEN
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.validateUserRegisterRequest(request)
        );
        //THEN
        assertEquals(ERROR_CODE.INVALID_PARAMETER.getErrorCode(), exception.getCode());
        assertEquals("password is invalid", exception.getTitle());
    }

    //OTP UNIT TEST
    @Test
    void sendOtpFirstTime_success() {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0982573860");
        request.setPassword("Password@123");

        //first time
        OtpLimitEntity otpLimitEntity = new OtpLimitEntity();
        otpLimitEntity.setPhoneNumber("84982573860");
        otpLimitEntity.setDailyOtpCounter(0);
        when(otpLimitRedisRepository.findById("84982573860")).thenReturn(Optional.of(otpLimitEntity));

        RegisterResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertNotNull(response.getTransactionId());
    }

    @Test
    void sendOtpFirstTime_userExistes_failed() {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0982573860");
        request.setPassword("Password@123");

        UserEntity existingUser = new UserEntity();
        existingUser.setPhoneNumber("84982573860");

        when(userRepository.findByPhoneNumber("84982573860")).thenReturn(existingUser);

        //THEN
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                userService.registerUser(request)
        );

        assertEquals("PhoneNumber is already exists", exception.getTitle());
    }

    //RESEND OTP UNIT TEST
    @Test
    void resendOtpBefore120s_failed() {
        // GIVEN
        String transactionId = "test_transaction_id";

        RegisterUserEntity entity = new RegisterUserEntity();
        entity.setTransactionId(transactionId);
        entity.setPhoneNumber("0982573860");
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
    void resendOtpAfter120s_failed() {
        // GIVEN
        String transactionId = "test_transaction_id";

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
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0982573860");
        request.setPassword("Password@123");

        OtpLimitEntity otpLimitEntity = new OtpLimitEntity();
        otpLimitEntity.setPhoneNumber("84982573860");
        otpLimitEntity.setDailyOtpCounter(5);
        when(otpLimitRedisRepository.findById("84982573860")).thenReturn(Optional.of(otpLimitEntity));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("OTP limit reached", ex.getTitle());
    }

    @Test
    void resendOTP_success() {
        String transactionId = "transaction_id";

        OtpLimitEntity otpLimitEntity = new OtpLimitEntity();
        otpLimitEntity.setPhoneNumber("84982573860");
        otpLimitEntity.setDailyOtpCounter(3);

        RegisterUserEntity registerUserEntity = new RegisterUserEntity();
        registerUserEntity.setTransactionId(transactionId);

        when(registerUserRedisRepository.findById(transactionId)).thenReturn(Optional.of(registerUserEntity));
        when(otpLimitRedisRepository.findById("84982573860")).thenReturn(Optional.of(otpLimitEntity));

        RegisterResponse response = userService.resendOTP(transactionId);

        // THEN
        assertNotNull(response);
        assertEquals(transactionId, response.getTransactionId());
    }

    //CONFIRM OTP UNIT TEST
    @Test
    void confirmOtpAfterExpired_failed() {
        String transactionId = "transaction_id";

        OtpLimitEntity otpLimitEntity = new OtpLimitEntity();
        otpLimitEntity.setPhoneNumber("84982573860");
        otpLimitEntity.setDailyOtpCounter(3);

        RegisterUserEntity registerUserEntity = new RegisterUserEntity();
        registerUserEntity.setTransactionId(transactionId);
        registerUserEntity.setOtp("123456");

        when(registerUserRedisRepository.findById(transactionId)).thenReturn(Optional.of(registerUserEntity));
        RegisterUserEntity otp = registerUserRedisRepository.findById(transactionId).get();
        otp.setOtpExpiredTime(System.currentTimeMillis()/1000 - 120);
        when(registerUserRedisRepository.findById(transactionId)).thenReturn(Optional.of(otp));

        ConfirmOtpRegisterRequest confirm = new ConfirmOtpRegisterRequest();
        confirm.setTransactionId(transactionId);
        confirm.setOtp(otp.getOtp());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            userService.confirmOtpRegister(confirm);
        });

        assertEquals("OTP has expired", ex.getTitle());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void confirmOtpNotReachedLimit_success(int otpFailCount) {
        String transactionId = "transaction_id";

        RegisterUserEntity registerUserEntity = new RegisterUserEntity();
        registerUserEntity.setTransactionId(transactionId);
        registerUserEntity.setPhoneNumber("84982573860");
        registerUserEntity.setPassword("password");
        registerUserEntity.setOtp("123456");
        registerUserEntity.setOtpFail(otpFailCount);
        registerUserEntity.setOtpExpiredTime(System.currentTimeMillis() / 1000 + 120);

        when(registerUserRedisRepository.findById(transactionId))
                .thenReturn(Optional.of(registerUserEntity));

        ConfirmOtpRegisterRequest confirm = new ConfirmOtpRegisterRequest();
        confirm.setTransactionId(transactionId);
        confirm.setOtp("123456");

        assertDoesNotThrow(() -> userService.confirmOtpRegister(confirm));
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6})
    void confirmOtpReachedLimit_failed(int otpFailCount) {
        String transactionId = "transaction_id";

        RegisterUserEntity registerUserEntity = new RegisterUserEntity();
        registerUserEntity.setTransactionId(transactionId);
        registerUserEntity.setPhoneNumber("84982573860");
        registerUserEntity.setPassword("password");
        registerUserEntity.setOtp("123456");
        registerUserEntity.setOtpFail(otpFailCount);
        registerUserEntity.setOtpExpiredTime(System.currentTimeMillis() / 1000 + 120);

        when(registerUserRedisRepository.findById(transactionId))
                .thenReturn(Optional.of(registerUserEntity));

        ConfirmOtpRegisterRequest confirm = new ConfirmOtpRegisterRequest();
        confirm.setTransactionId(transactionId);
        confirm.setOtp("123456");

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            userService.confirmOtpRegister(confirm);
        });

        assertEquals("input OTP attempts limit reached", ex.getTitle());
    }

    @Test
    void confirmOtpIncorrect_failed() {
        String transactionId = "transaction_id";

        OtpLimitEntity otpLimitEntity = new OtpLimitEntity();
        otpLimitEntity.setPhoneNumber("84982573860");
        otpLimitEntity.setDailyOtpCounter(3);

        RegisterUserEntity registerUserEntity = new RegisterUserEntity();
        registerUserEntity.setTransactionId(transactionId);
        registerUserEntity.setOtp("123456");

        when(registerUserRedisRepository.findById(transactionId)).thenReturn(Optional.of(registerUserEntity));
        RegisterUserEntity otp = registerUserRedisRepository.findById(transactionId).get();
        otp.setOtpExpiredTime(System.currentTimeMillis()/1000 + 120);
        when(registerUserRedisRepository.findById(transactionId)).thenReturn(Optional.of(otp));

        ConfirmOtpRegisterRequest confirm = new ConfirmOtpRegisterRequest();
        confirm.setTransactionId(transactionId);
        confirm.setOtp("23456");

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            userService.confirmOtpRegister(confirm);
        });

        assertEquals("OTP is incorrect", ex.getTitle());
    }
}
