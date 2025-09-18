package com.carrent.carrental.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.carrent.carrental.dto.PaymentDTO;
import com.carrent.carrental.entity.Booking;
import com.carrent.carrental.entity.Payment;
import com.carrent.carrental.enums.PaymentStatus;
import com.carrent.carrental.mappers.PaymentMapper;
import com.carrent.carrental.repository.BookingRepository;
import com.carrent.carrental.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository,BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository=bookingRepository;
    }

   public PaymentDTO createPayment(PaymentDTO paymentDTO) {
    // Load the booking from repository
    Booking booking = bookingRepository.findById(paymentDTO.getBookingId())
            .orElseThrow(() -> new RuntimeException("Booking not found"));

    Payment payment = PaymentMapper.toEntity(paymentDTO, booking);
    Payment savedPayment = paymentRepository.save(payment);
    return PaymentMapper.toDTO(savedPayment);
}


    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(PaymentMapper::toDTO).toList();
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return PaymentMapper.toDTO(payment);
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        existingPayment.setAmount(paymentDTO.getAmount());
        existingPayment.setStatus(paymentDTO.getStatus() != null ? PaymentStatus.valueOf(paymentDTO.getStatus()) : null);

        Payment updatedPayment = paymentRepository.save(existingPayment);
        return PaymentMapper.toDTO(updatedPayment);
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) throw new RuntimeException("Payment not found");
        paymentRepository.deleteById(id);
    }
}
