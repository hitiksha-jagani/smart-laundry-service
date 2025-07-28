package com.SmartLaundry.exception;

public class BadCredException extends RuntimeException{
    public BadCredException(){
        super("Invalid username or password.");
    }
}
