package com.ecorides.service;

import com.ecorides.payload.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto);

    BookingDto getBookingById(Long bookingId);

    List<BookingDto> getAllBookings();

    List<BookingDto> getBookingsByUser(Long userId);

    List<BookingDto> getBookingsByCar(Long carId);

    List<BookingDto> getBookingsByLocation(Long locationId);

    BookingDto updateBooking(Long bookingId, BookingDto bookingDto);

    void cancelBooking(Long bookingId, String reason);

    BookingDto startBooking(Long bookingId);

    BookingDto completeBooking(Long bookingId);

}