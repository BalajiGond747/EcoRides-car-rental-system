package com.carrent.carrental.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrent.carrental.dto.BookingDTO;
import com.carrent.carrental.entity.User;
import com.carrent.carrental.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO,
            @AuthenticationPrincipal User principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(bookingDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isBookingOwner(#id, principal.id)")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id, @AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isBookingOwner(#id, principal.id)")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO,
            @AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(bookingService.updateBooking(id, bookingDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isBookingOwner(#id, principal.id)")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id, @AuthenticationPrincipal User principal) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
