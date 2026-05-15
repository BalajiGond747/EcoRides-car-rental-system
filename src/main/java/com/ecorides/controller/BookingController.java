package com.ecorides.controller;

import com.ecorides.payload.dto.BookingDto;
import com.ecorides.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto dto) {
        return bookingService.createBooking(dto);
    }


    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping
    public List<BookingDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/user/{userId}")
    public List<BookingDto> getUserBookings(@PathVariable Long userId) {
        return bookingService.getBookingsByUser(userId);
    }

    @GetMapping("/car/{carId}")
    public List<BookingDto> getCarBookings(@PathVariable Long carId) {
        return bookingService.getBookingsByCar(carId);
    }


    @PutMapping("/{id}")
    public BookingDto updateBooking(@PathVariable Long id,
                                    @RequestBody BookingDto dto) {
        return bookingService.updateBooking(id, dto);
    }

    @DeleteMapping("/{id}")
    public void cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
    }


    @PatchMapping("/{id}/start")
    public BookingDto start(@PathVariable Long id) {
        return bookingService.startBooking(id);
    }

    @PatchMapping("/{id}/complete")
    public BookingDto complete(@PathVariable Long id) {
        return bookingService.completeBooking(id);
    }
}