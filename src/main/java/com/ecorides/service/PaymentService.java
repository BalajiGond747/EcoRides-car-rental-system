package com.ecorides.service;

import com.ecorides.payload.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {

    PaymentDTO createOrder(Long bookingId);

    void verifyPayment(PaymentDTO dto);

    List<PaymentDTO> getAllPayments();

}