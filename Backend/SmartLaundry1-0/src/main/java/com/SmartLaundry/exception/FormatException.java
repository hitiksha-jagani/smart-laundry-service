package com.SmartLaundry.exception;

public class FormatException extends RuntimeException{
    public FormatException(String fieldName){
        super("Invalid format for " + fieldName + ".");
    }
}
