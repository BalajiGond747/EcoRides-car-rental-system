package com.carrent.carrental.mappers;

import java.time.LocalDateTime;

import com.carrent.carrental.dto.PaymentDTO;
import com.carrent.carrental.entity.Booking;
import com.carrent.carrental.entity.Payment;
import com.carrent.carrental.enums.PaymentStatus;

public class PaymentMapper {

    // Entity -> DTO
    public static PaymentDTO toDTO(Payment payment) {
        if (payment == null) return null;

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking() != null ? payment.getBooking().getId() : null);
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : null);
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionId(payment.getTransactionId());

        return dto;
    }

    // DTO -> Entity
    public static Payment toEntity(PaymentDTO dto, Booking booking) {
        if (dto == null) return null;

        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setBooking(booking);  // must pass Booking entity
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate() != null ? dto.getPaymentDate() : LocalDateTime.now());
        payment.setStatus(dto.getStatus() != null ? PaymentStatus.valueOf(dto.getStatus()) : PaymentStatus.PENDING);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setTransactionId(dto.getTransactionId());

        return payment;
    }
}
