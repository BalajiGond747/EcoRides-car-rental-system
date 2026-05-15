package com.ecorides.exception;

import lombok.Getter;

@Getter
public class CarException extends RuntimeException {

    private final int statusCode;

    public CarException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}