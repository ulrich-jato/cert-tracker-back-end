package com.devops.certtracker.exception;

import com.devops.certtracker.dto.request.ChangePasswordRequest;

public class ChangePasswordException extends RuntimeException{
    public ChangePasswordException(String message){
        super(message);
    }

    public ChangePasswordException(String message, Throwable cause){
        super(message, cause);
    }

    public ChangePasswordException(Throwable cause){
        super(cause);
    }

}
