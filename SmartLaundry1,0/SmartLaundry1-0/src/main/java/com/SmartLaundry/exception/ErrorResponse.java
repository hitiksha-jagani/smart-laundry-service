package com.SmartLaundry.exception;

import lombok.*;

// Store error related data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
}
