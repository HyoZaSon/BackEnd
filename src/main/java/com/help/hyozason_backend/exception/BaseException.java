package com.help.hyozason_backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private final String errorCode;
    private String message;
    private final HttpStatus status;

    public BaseException(ErrorCode code) {
       errorCode = code.getErrorCode();
        message = code.getMessage();
        status = code.getStatus();
    }

    public void setEmailMessage(String message) {
        this.message = "해당 사용자의 이메일은 " + message + " 입니다.";
    }
}
