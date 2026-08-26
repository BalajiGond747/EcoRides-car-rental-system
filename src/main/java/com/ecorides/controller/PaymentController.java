package com.ecorides.controller;

import com.ecorides.domain.PaymentMethod;
import com.ecorides.domain.PaymentStatus;
import com.ecorides.payload.dto.PaymentDTO;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentDTO>> createOrder(@PathVariable Long bookingId) {

        PaymentDTO payment = paymentService.createOrder(bookingId);

        return ResponseEntity.ok(ApiResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment order created successfully")
                .data(payment)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Object>> verify(@Valid @RequestBody PaymentDTO dto) {

        paymentService.verifyPayment(dto);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Payment verified successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PaymentDTO>>> getAllPayments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search, @RequestParam(required = false) PaymentStatus status, @RequestParam(required = false) PaymentMethod paymentMethod, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir) {

        PageResponse<PaymentDTO> payments = paymentService.getAllPayments(page, size, search, status, paymentMethod, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.<PageResponse<PaymentDTO>>builder()
                .success(true)
                .message("Payments fetched successfully")
                .data(payments)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByBooking(@PathVariable Long bookingId) {

        PaymentDTO payment = paymentService.getPaymentByBooking(bookingId);

        return ResponseEntity.ok(ApiResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment fetched successfully")
                .data(payment)
                .timestamp(LocalDateTime.now())
                .build());
    }

}