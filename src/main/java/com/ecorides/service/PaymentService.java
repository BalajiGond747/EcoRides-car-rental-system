package com.ecorides.service;

import com.ecorides.payload.dto.PaymentDTO;

public interface PaymentService {

    PaymentDTO createOrder(Long bookingId);

    void verifyPayment(PaymentDTO dto);

}