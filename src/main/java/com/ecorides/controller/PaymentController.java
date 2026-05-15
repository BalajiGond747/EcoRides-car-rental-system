package com.ecorides.controller;

import com.ecorides.payload.dto.PaymentDto;
import com.ecorides.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/create-order/{bookingId}")
    public PaymentDto createOrder(@PathVariable Long bookingId) {
        return paymentService.createOrder(bookingId);
    }


    @PostMapping("/verify")
    public String verify(@RequestBody PaymentDto dto) {
        paymentService.verifyPayment(dto);
        return "Payment Successful";
    }
}