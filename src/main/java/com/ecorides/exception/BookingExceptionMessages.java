package com.ecorides.exception;

public class BookingExceptionMessages {

    public static final String BOOKING_NOT_FOUND = "Booking not found";
    public static final String CAR_NOT_AVAILABLE = "Car is not available for booking";
    public static final String CAR_NOT_ACTIVE = "Car is inactive";
    public static final String BOOKING_OVERLAP = "Car is already booked for the selected time range";
    public static final String INVALID_TIME = "Invalid booking time range";
    public static final String INVALID_STATUS_TRANSITION = "Invalid booking status transition";

    private BookingExceptionMessages() {}
}