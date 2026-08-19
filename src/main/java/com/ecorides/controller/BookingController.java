package com.ecorides.controller;

import com.ecorides.payload.dto.BookingDto;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(@Valid @RequestBody BookingDto dto) {

        BookingDto booking = bookingService.createBooking(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BookingDto>builder()
                        .success(true)
                        .message("Booking created successfully")
                        .data(booking)
                        .timestamp(LocalDateTime.now())
                        .build()

                );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingDto>> getBooking(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.<BookingDto>builder()
                .success(true)
                .message("Booking fetched successfully")
                .data(bookingService.getBookingById(id))
                .timestamp(LocalDateTime.now())
                .build()

        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getAllBookings() {

        return ResponseEntity.ok(ApiResponse.<List<BookingDto>>builder()
                .success(true)
                .message("Bookings fetched successfully")
                .data(bookingService.getAllBookings())
                .timestamp(LocalDateTime.now())
                .build());

    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getUserBookings(@PathVariable Long userId) {

        return ResponseEntity.ok(ApiResponse.<List<BookingDto>>builder()
                .success(true)
                .message("User bookings fetched")
                .data(bookingService.getBookingsByUser(userId))
                .timestamp(LocalDateTime.now())
                .build()

        );
    }

    @GetMapping("/car/{carId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getCarBookings(@PathVariable Long carId) {

        return ResponseEntity.ok(ApiResponse.<List<BookingDto>>builder()
                .success(true)
                .message("Car bookings fetched")
                .data(bookingService.getBookingsByCar(carId))
                .timestamp(LocalDateTime.now())
                .build()

        );

    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getLocationBookings(@PathVariable Long locationId) {

        return ResponseEntity.ok(ApiResponse.<List<BookingDto>>builder()
                .success(true)
                .message("Location bookings fetched")
                .data(bookingService.getBookingsByLocation(locationId))
                .timestamp(LocalDateTime.now())
                .build()

        );

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingDto>> updateBooking(@PathVariable Long id, @Valid @RequestBody BookingDto dto) {

        return ResponseEntity.ok(ApiResponse.<BookingDto>builder()
                .success(true)
                .message("Booking updated")
                .data(bookingService.updateBooking(id, dto))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Object>> cancelBooking(@PathVariable Long id, @RequestParam String reason) {

        bookingService.cancelBooking(id, reason);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Booking cancelled")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingDto>> startBooking(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.<BookingDto>builder()
                .success(true)
                .message("Booking started")
                .data(bookingService.startBooking(id))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingDto>> completeBooking(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.<BookingDto>builder()
                .success(true)
                .message("Booking completed")
                .data(bookingService.completeBooking(id))
                .timestamp(LocalDateTime.now())
                .build());
    }

}