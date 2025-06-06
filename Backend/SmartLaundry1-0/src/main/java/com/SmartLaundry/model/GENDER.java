package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GENDER {
    MALE,
    FEMALE,
    OTHER;

    // Validate before deserialization error
    @JsonCreator
    public static GENDER fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Gender is required.");
        }
        try {
            return GENDER.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid gender: " + value + ". Allowed values are: MALE, FEMALE, OTHER.");
//            throw new IllegalArgumentException("Invalid gender: " + value + ". Allowed values are: MALE, FEMALE, OTHER.");
        }
    }

    @JsonValue
    public String toValue(){
        return this.name();
    }
}
