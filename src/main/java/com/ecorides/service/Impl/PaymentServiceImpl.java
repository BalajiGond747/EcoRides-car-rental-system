package com.ecorides.service.Impl;

import com.ecorides.config.RazorpayProperties;
import com.ecorides.domain.BookingStatus;
import com.ecorides.domain.PaymentStatus;
import com.ecorides.entity.Booking;
import com.ecorides.entity.Payment;
import com.ecorides.exception.PaymentException;
import com.ecorides.payload.dto.PaymentDto;
import com.ecorides.repository.BookingRepository;
import com.ecorides.repository.PaymentRepository;
import com.ecorides.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final RazorpayProperties properties;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaymentDto createOrder(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new PaymentException("Booking not found"));

        try {
            JSONObject options = new JSONObject();
            options.put("amount", booking.getTotalAmount() * 100);
            options.put("currency", "INR");
            options.put("receipt", "order_" + booking.getId());

            Order order = razorpayClient.orders.create(options);

            Payment payment = Payment.builder()
                    .booking(booking)
                    .amount(booking.getTotalAmount())
                    .paymentMethod("RAZORPAY")
                    .razorpayOrderId(order.get("id"))
                    .status(PaymentStatus.PENDING)
                    .build();

            paymentRepository.save(payment);

            return PaymentDto.builder()
                    .bookingId(bookingId)
                    .amount(booking.getTotalAmount())
                    .razorpayOrderId(order.get("id"))
                    .key(properties.getKeyId())
                    .build();

        } catch (Exception e) {
            throw new PaymentException("Error creating Razorpay order");
        }
    }

    @Override
    @Transactional
    public void verifyPayment(PaymentDto dto) {

        Payment payment = paymentRepository.findByRazorpayOrderId(dto.getRazorpayOrderId())
                .orElseThrow(() -> new PaymentException("Payment not found"));

        try {
            String payload = dto.getRazorpayOrderId() + "|" + dto.getRazorpayPaymentId();

            String expectedSignature = hmacSha256(payload, properties.getKeySecret());

            if (!expectedSignature.equals(dto.getRazorpaySignature())) {
                throw new PaymentException("Invalid payment signature");
            }

            payment.setRazorpayPaymentId(dto.getRazorpayPaymentId());
            payment.setRazorpaySignature(dto.getRazorpaySignature());
            payment.setStatus(PaymentStatus.SUCCESS);


            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);

            paymentRepository.save(payment);

        } catch (Exception e) {
            throw new PaymentException("Payment verification failed");
        }
    }

    private String hmacSha256(String data, String secret) {

        return data;
    }
}