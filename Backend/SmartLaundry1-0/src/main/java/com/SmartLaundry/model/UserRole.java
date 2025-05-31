package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMIN,
    SERVICE_PROVIDER,
    DELIVERY_AGENT,
    CUSTOMER;

    // Validate before deserialization error
    @JsonCreator
    public static UserRole fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role is required.");
        }
        try {
            return UserRole.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid role: " + value + ". Allowed values are: CUSTOMER, ADMIN, SERVICE_PROVIDER, DELIVERY_AGENT.");
        }
    }

    @JsonValue
    public String toValue(){
        return this.name();
    }
}
