package com.devops.certtracker.exception;

public class PasswordResetException extends RuntimeException{
    public PasswordResetException(String message){
        super(message);
    }
}
