package com.ecorides.service;

import com.ecorides.domain.PaymentMethod;
import com.ecorides.domain.PaymentStatus;
import com.ecorides.payload.dto.PaymentDTO;
import com.ecorides.payload.response.PageResponse;

public interface PaymentService {

    PaymentDTO createOrder(Long bookingId);

    void verifyPayment(PaymentDTO dto);

    PageResponse<PaymentDTO> getAllPayments(int page, int size, String search, PaymentStatus status, PaymentMethod paymentMethod, String sortBy, String sortDir);
}