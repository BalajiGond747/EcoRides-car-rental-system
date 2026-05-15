package com.ecorides.service;

import com.ecorides.payload.dto.PaymentDto;

public interface PaymentService {

    PaymentDto createOrder(Long bookingId);

    void verifyPayment(PaymentDto dto);
}