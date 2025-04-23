package com.gtel.srpingtutorial.exception;

import com.gtel.srpingtutorial.utils.ERROR_CODE;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationException extends RuntimeException {
    private String code;
    private Map<String, Object> data;
    private String title;
    private HttpStatus httpStatus;

    public ApplicationException(ERROR_CODE errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getErrorCode();
        this.title = errorCode.getMessage();
    }

    public ApplicationException(ERROR_CODE errorCode, String message) {
        super(message);
        this.code = errorCode.getErrorCode();
        this.title = message;
    }
    public ApplicationException(ERROR_CODE errorCode, HttpStatus httpStatus) {
        super(errorCode.getMessage());
        this.code = errorCode.getErrorCode();
        this.title = errorCode.getMessage();
        this.httpStatus = httpStatus;
    }

    public ApplicationException(ERROR_CODE errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.code = errorCode.getErrorCode();
        this.title = message;
        this.httpStatus = httpStatus;
    }

}