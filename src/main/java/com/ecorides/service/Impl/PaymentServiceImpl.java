package com.ecorides.service.Impl;

import com.ecorides.config.RazorpayProperties;
import com.ecorides.domain.BookingStatus;
import com.ecorides.domain.PaymentMethod;
import com.ecorides.domain.PaymentStatus;
import com.ecorides.domain.UserRole;
import com.ecorides.entity.Booking;
import com.ecorides.entity.Payment;
import com.ecorides.entity.User;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.PaymentException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.payload.dto.PaymentDTO;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.repository.BookingRepository;
import com.ecorides.repository.PaymentRepository;
import com.ecorides.repository.UserRepository;
import com.ecorides.service.InvoiceService;
import com.ecorides.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final RazorpayProperties properties;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final InvoiceService invoiceService;

    @Override
    public PaymentDTO createOrder(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        User currentUser = getAuthenticatedUser();

        if (currentUser.getUserRole() != UserRole.ADMIN && !booking.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException("You do not have permission to make payment for this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Payment can only be created for a pending booking");
        }

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElse(null);

        if (payment != null && payment.getStatus() != PaymentStatus.FAILED) {

            throw new BadRequestException("Payment already exists");
        }

        try {

            JSONObject options = new JSONObject();

            options.put("amount", booking.getTotalAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .intValue());

            options.put("currency", "INR");
            options.put("receipt", "booking_" + booking.getId());

            Order order = razorpayClient.orders.create(options);

            if (payment == null) {

                payment = Payment.builder()
                        .booking(booking)
                        .build();
            }

            payment.setAmount(booking.getTotalAmount());
            payment.setPaymentMethod(PaymentMethod.RAZORPAY);
            payment.setRazorpayOrderId(order.get("id"));
            payment.setRazorpayPaymentId(null);
            payment.setRazorpaySignature(null);
            payment.setStatus(PaymentStatus.PENDING);

            paymentRepository.save(payment);

            return PaymentDTO.builder()
                    .bookingId(bookingId)
                    .amount(booking.getTotalAmount())
                    .paymentMethod(PaymentMethod.RAZORPAY)
                    .razorpayOrderId(order.get("id"))
                    .status(PaymentStatus.PENDING)
                    .key(properties.getKeyId())
                    .build();

        } catch (Exception ex) {

            throw new PaymentException("Error creating payment order");
        }
    }

    @Override
    public void verifyPayment(PaymentDTO dto) {

        if (dto.getRazorpayOrderId() == null || dto.getRazorpayPaymentId() == null || dto.getRazorpaySignature() == null) {

            throw new BadRequestException("Payment verification details are required");
        }

        Payment payment = paymentRepository.findByRazorpayOrderId(dto.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment already verified");
        }

        try {

            String payload = dto.getRazorpayOrderId() + "|" + dto.getRazorpayPaymentId();

            String expectedSignature = hmacSha256(payload, properties.getKeySecret());

            if (!expectedSignature.equals(dto.getRazorpaySignature())) {

                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                throw new BadRequestException("Invalid payment signature");
            }
            Booking booking = payment.getBooking();

            if (booking.getStatus() != BookingStatus.PENDING) {
                throw new BadRequestException("Booking is not pending");
            }
            payment.setRazorpayPaymentId(dto.getRazorpayPaymentId());
            payment.setRazorpaySignature(dto.getRazorpaySignature());
            payment.setStatus(PaymentStatus.SUCCESS);
            booking.setStatus(BookingStatus.CONFIRMED);

            paymentRepository.save(payment);
            invoiceService.generateInvoice(booking.getId());

        } catch (PaymentException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new PaymentException("Payment verification failed");
        }
    }

    private String hmacSha256(String data, String secret) {

        try {

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(key);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of()
                    .formatHex(hash);

        } catch (Exception ex) {
            throw new PaymentException("Signature generation failed");
        }
    }

    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName()
                .isBlank()) {

            throw new AccessDeniedException("Authenticated user not found");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentDTO> getAllPayments(int page, int size, String search, PaymentStatus status, PaymentMethod paymentMethod, String sortBy, String sortDir) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        Set<String> allowedSortFields = Set.of("id", "amount", "status", "paymentMethod", "createdAt", "updatedAt");

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        boolean hasSearch = search != null && !search.trim()
                .isEmpty();

        Page<Payment> paymentPage;

        if (hasSearch && status != null && paymentMethod != null) {

            paymentPage = paymentRepository.findBySearchAndStatusAndPaymentMethod(search.trim(), status, paymentMethod, pageable);

        } else if (hasSearch && status != null) {

            paymentPage = paymentRepository.findBySearchAndStatus(search.trim(), status, pageable);

        } else if (hasSearch && paymentMethod != null) {

            paymentPage = paymentRepository.findBySearchAndPaymentMethod(search.trim(), paymentMethod, pageable);

        } else if (hasSearch) {

            paymentPage = paymentRepository.findBySearch(search.trim(), pageable);

        } else if (status != null && paymentMethod != null) {

            paymentPage = paymentRepository.findByStatusAndPaymentMethod(status, paymentMethod, pageable);

        } else if (status != null) {

            paymentPage = paymentRepository.findByStatus(status, pageable);

        } else if (paymentMethod != null) {

            paymentPage = paymentRepository.findByPaymentMethod(paymentMethod, pageable);

        } else {

            paymentPage = paymentRepository.findAll(pageable);
        }

        List<PaymentDTO> payments = paymentPage.getContent()
                .stream()
                .map(payment -> PaymentDTO.builder()
                        .id(payment.getId())
                        .bookingId(payment.getBooking()
                                .getId())
                        .amount(payment.getAmount())
                        .paymentMethod(payment.getPaymentMethod())
                        .status(payment.getStatus())
                        .razorpayOrderId(payment.getRazorpayOrderId())
                        .razorpayPaymentId(payment.getRazorpayPaymentId())
                        .razorpaySignature(payment.getRazorpaySignature())
                        .createdAt(payment.getCreatedAt())
                        .updatedAt(payment.getUpdatedAt())
                        .build())
                .toList();

        return PageResponse.<PaymentDTO>builder()
                .content(payments)
                .page(paymentPage.getNumber())
                .size(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .first(paymentPage.isFirst())
                .last(paymentPage.isLast())
                .build();
    }

}