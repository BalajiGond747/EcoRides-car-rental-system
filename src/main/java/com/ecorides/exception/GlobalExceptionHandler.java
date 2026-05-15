package com.ecorides.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handleUserException(UserException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "message", ex.getMessage(),
                        "status", ex.getStatusCode()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(500)
                .body(Map.of(
                        "message", "Internal Server Error",
                        "status", 500
                ));
    }
    @ExceptionHandler(CarException.class)
    public ResponseEntity<?> handleCarException(CarException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "message", ex.getMessage(),
                        "status", ex.getStatusCode()
                ));
    }
}