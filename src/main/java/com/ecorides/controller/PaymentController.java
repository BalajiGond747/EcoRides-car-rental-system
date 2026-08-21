package com.ecorides.controller;

import com.ecorides.payload.dto.PaymentDTO;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getAllPayments() {

        List<PaymentDTO> payments = paymentService.getAllPayments();

        return ResponseEntity.ok(ApiResponse.<List<PaymentDTO>>builder()
                .success(true)
                .message("Payments fetched successfully")
                .data(payments)
                .timestamp(LocalDateTime.now())
                .build());
    }

}