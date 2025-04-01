package com.gtel.srpingtutorial.model.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phoneNumber;

    private String password;
}
