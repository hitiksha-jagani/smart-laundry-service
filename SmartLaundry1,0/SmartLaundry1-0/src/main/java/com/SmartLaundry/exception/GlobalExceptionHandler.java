package com.SmartLaundry.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.stream.Collectors;

// Single point to handle all exceptions
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponse(Exception ex, HttpStatus status, HttpServletRequest req) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(FieldBlankException.class)
    public ResponseEntity<ErrorResponse> handleFieldBlank(FieldBlankException ex, HttpServletRequest req) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(FormatException.class)
    public ResponseEntity<ErrorResponse> handleFormat(FormatException ex, HttpServletRequest req) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest req) {
        return buildResponse(ex, HttpStatus.CONFLICT, req);
    }

    @ExceptionHandler(PasswordMisMatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMisMatchException ex, HttpServletRequest req) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentials(BadCredentialsException ex, HttpServletRequest req){
        return buildResponse(ex, HttpStatus.UNAUTHORIZED, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest req) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest req) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, req);
    }

}
