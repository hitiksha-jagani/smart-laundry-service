package com.SmartLaundry.exception;

public class FieldBlankException extends RuntimeException{
    public FieldBlankException(String fieldName){
        super(fieldName + " is required.");
    }
}
