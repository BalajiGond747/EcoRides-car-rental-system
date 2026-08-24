package com.ecorides.service;

import com.ecorides.domain.BookingStatus;
import com.ecorides.payload.dto.BookingDto;
import com.ecorides.payload.response.PageResponse;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto);

    BookingDto getBookingById(Long bookingId);

    PageResponse<BookingDto> getAllBookings(int page, int size, String search, BookingStatus status, String sortBy, String sortDir);

    List<BookingDto> getBookingsByUser(Long userId);

    List<BookingDto> getBookingsByCar(Long carId);

    List<BookingDto> getBookingsByLocation(Long locationId);

    BookingDto updateBooking(Long bookingId, BookingDto bookingDto);

    void cancelBooking(Long bookingId, String reason);

    BookingDto startBooking(Long bookingId);

    BookingDto completeBooking(Long bookingId);

}