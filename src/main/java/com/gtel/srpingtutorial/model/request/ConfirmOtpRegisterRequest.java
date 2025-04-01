package com.gtel.srpingtutorial.model.request;

import lombok.Data;

@Data
public class ConfirmOtpRegisterRequest {
    private String transactionId;
    private String otp;
}
