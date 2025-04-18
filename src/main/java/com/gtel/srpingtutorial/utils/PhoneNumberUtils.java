package com.gtel.srpingtutorial.utils;

import com.gtel.srpingtutorial.exception.ApplicationException;
import org.apache.commons.lang3.StringUtils;

public class PhoneNumberUtils {

    public static String validatePhoneNumber(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
        }

        phoneNumber = phoneNumber.replaceAll(" " , "");
        // check length :
        //+84982573860
        //0982573860
        //84982573860

        if (phoneNumber.length() < 10 || phoneNumber.length() > 12) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
        }

        if (!phoneNumber.matches("^\\+?\\d+$")) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER, "phoneNumber is invalid");
        }

        if(phoneNumber.length() == 10) {
            if (!phoneNumber.startsWith("0")) {
                throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
            }
                return  "84" + phoneNumber.substring(1);
        }

        if(phoneNumber.length() == 12) {
            if(!phoneNumber.startsWith("+84")) {
                throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
            }
            return phoneNumber.substring(1);
        }

        if(!phoneNumber.startsWith("84")) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
        }
        return phoneNumber;

    }


    public static void main(String[] args) {
        System.out.println(validatePhoneNumber("+84982573860"));
        System.out.println(validatePhoneNumber("84982573860"));
        System.out.println(validatePhoneNumber("0982573860"));
    }
}
