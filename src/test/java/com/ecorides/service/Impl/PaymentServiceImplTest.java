package com.ecorides.service.Impl;

import com.ecorides.config.RazorpayProperties;
import com.ecorides.domain.BookingStatus;
import com.ecorides.domain.PaymentMethod;
import com.ecorides.domain.PaymentStatus;
import com.ecorides.entity.Booking;
import com.ecorides.entity.Payment;
import com.ecorides.entity.User;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.PaymentException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.payload.dto.PaymentDTO;
import com.ecorides.repository.BookingRepository;
import com.ecorides.repository.PaymentRepository;
import com.ecorides.repository.UserRepository;
import com.ecorides.service.InvoiceService;
import com.razorpay.Order;
import com.razorpay.OrderClient;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private RazorpayClient razorpayClient;

    @Mock
    private OrderClient orderClient;

    @Mock
    private RazorpayProperties properties;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private Order razorpayOrder;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Booking booking;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("customer@gmail.com");

        booking = new Booking();
        booking.setId(100L);
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalAmount(new BigDecimal("5900.00"));

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("customer@gmail.com", null));
    }

    @Test
    void shouldCreatePaymentOrderSuccessfully() throws Exception {

        when(userRepository.findByEmail("customer@gmail.com")).thenReturn(Optional.of(user));

        ReflectionTestUtils.setField(razorpayClient, "orders", orderClient);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        when(paymentRepository.findByBookingId(100L)).thenReturn(Optional.empty());

        when(orderClient.create(any(JSONObject.class))).thenReturn(razorpayOrder);

        when(razorpayOrder.get("id")).thenReturn("order_test123");

        when(properties.getKeyId()).thenReturn("test_key");

        Payment savedPayment = Payment.builder()
                .booking(booking)
                .amount(new BigDecimal("5900.00"))
                .paymentMethod(PaymentMethod.RAZORPAY)
                .razorpayOrderId("order_test123")
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PaymentDTO result = paymentService.createOrder(100L);

        assertEquals(100L, result.getBookingId());
        assertEquals(0, new BigDecimal("5900.00").compareTo(result.getAmount()));
        assertEquals(PaymentMethod.RAZORPAY, result.getPaymentMethod());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertEquals("order_test123", result.getRazorpayOrderId());
        assertEquals("test_key", result.getKey());

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldRejectWhenBookingDoesNotExist() {

        when(bookingRepository.findById(100L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> paymentService.createOrder(100L));

        assertEquals("Booking not found with id: 100", exception.getMessage());
    }

    @Test
    void shouldRejectPaymentWhenBookingIsNotPending() {

        booking.setStatus(BookingStatus.CONFIRMED);

        when(userRepository.findByEmail("customer@gmail.com")).thenReturn(Optional.of(user));

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paymentService.createOrder(100L));

        assertEquals("Payment can only be created for a pending booking", exception.getMessage());
    }

    @Test
    void shouldRejectWhenPaymentAlreadyExists() {

        Payment existingPayment = Payment.builder()
                .booking(booking)
                .status(PaymentStatus.PENDING)
                .build();

        when(userRepository.findByEmail("customer@gmail.com")).thenReturn(Optional.of(user));

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        when(paymentRepository.findByBookingId(100L)).thenReturn(Optional.of(existingPayment));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paymentService.createOrder(100L));

        assertEquals("Payment already exists", exception.getMessage());
    }

    @Test
    void shouldRejectVerificationWhenDetailsAreMissing() {

        PaymentDTO dto = PaymentDTO.builder()
                .razorpayOrderId(null)
                .razorpayPaymentId(null)
                .razorpaySignature(null)
                .build();

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paymentService.verifyPayment(dto));

        assertEquals("Payment verification details are required", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidPaymentSignature() {

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(new BigDecimal("5900.00"))
                .paymentMethod(PaymentMethod.RAZORPAY)
                .status(PaymentStatus.PENDING)
                .razorpayOrderId("order_test123")
                .build();

        PaymentDTO dto = PaymentDTO.builder()
                .razorpayOrderId("order_test123")
                .razorpayPaymentId("pay_test123")
                .razorpaySignature("invalid_signature")
                .build();

        when(paymentRepository.findByRazorpayOrderId("order_test123")).thenReturn(Optional.of(payment));

        when(properties.getKeySecret()).thenReturn("test_secret");

        PaymentException exception = assertThrows(PaymentException.class, () -> paymentService.verifyPayment(dto));

        assertEquals("Payment verification failed", exception.getMessage());

        assertEquals(PaymentStatus.FAILED, payment.getStatus());

        verify(paymentRepository).save(payment);
    }

    @Test
    void shouldRejectAlreadyVerifiedPayment() {

        Payment payment = Payment.builder()
                .booking(booking)
                .status(PaymentStatus.SUCCESS)
                .razorpayOrderId("order_test123")
                .build();

        PaymentDTO dto = PaymentDTO.builder()
                .razorpayOrderId("order_test123")
                .razorpayPaymentId("pay_test123")
                .razorpaySignature("signature")
                .build();

        when(paymentRepository.findByRazorpayOrderId("order_test123")).thenReturn(Optional.of(payment));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paymentService.verifyPayment(dto));

        assertEquals("Payment already verified", exception.getMessage());
    }
}