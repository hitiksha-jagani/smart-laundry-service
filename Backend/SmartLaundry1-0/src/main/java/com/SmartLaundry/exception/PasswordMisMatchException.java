package com.SmartLaundry.exception;

public class PasswordMisMatchException extends RuntimeException{
    public PasswordMisMatchException(){
        super("Password and confirm password do not match.");
    }
}
