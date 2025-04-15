package com.gtel.srpingtutorial.utils;

import com.gtel.srpingtutorial.exception.ApplicationException;
import com.gtel.srpingtutorial.model.request.RegisterRequest;
import com.gtel.srpingtutorial.model.response.RegisterResponse;
import com.gtel.srpingtutorial.redis.entities.OtpLimitEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PhoneNumberUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "abcdef05", "038235943", "0345678765456", "8473249123", "03873249123", "084873249123"})
    void checkInvalidPhoneNumber_failed(String phoneNumber) {

        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                PhoneNumberUtils.validatePhoneNumber(phoneNumber)
        );

        assertEquals(ERROR_CODE.INVALID_PARAMETER.getErrorCode(), exception.getCode());
        assertEquals("phoneNumber is invalid", exception.getTitle());
    }

    @Test
    void checkValidPhoneNumberLength10_success() {
        String phoneNumber = "0321237523";

        String validated = PhoneNumberUtils.validatePhoneNumber(phoneNumber);
        //THEN
        assertTrue(validated.startsWith("84"));
        assertEquals(11, validated.length());
        assertEquals("84321237523", validated);
    }

    @Test
    void checkValidPhoneNumberLength11_success() {
        String phoneNumber = "84321237523";

        String validated = PhoneNumberUtils.validatePhoneNumber(phoneNumber);
        //THEN
        assertTrue(validated.startsWith("84"));
        assertEquals(11, validated.length());
        assertEquals("84321237523", validated);
    }

    @Test
    void checkValidPhoneNumberLength12_success() {

        String phoneNumber = "+84321237523";

        String validated = PhoneNumberUtils.validatePhoneNumber(phoneNumber);
        //THEN
        assertTrue(validated.startsWith("84"));
        assertEquals(11, validated.length());
        assertEquals("84321237523", validated);

    }
}
