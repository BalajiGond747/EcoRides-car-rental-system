package com.ecorides.service.Impl;

import com.ecorides.domain.OtpPurpose;
import com.ecorides.entity.OtpVerification;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.UserException;
import com.ecorides.payload.request.VerifyOtpRequest;
import com.ecorides.repository.OtpVerificationRepository;
import com.ecorides.repository.UserRepository;
import com.ecorides.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private OtpVerificationRepository otpRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OtpServiceImpl otpService;

    private OtpVerification otp;

    @BeforeEach
    void setUp() {

        otp = new OtpVerification();

        otp.setEmail("customer@gmail.com");
        otp.setPurpose(OtpPurpose.REGISTRATION);
        otp.setOtp("123456");
        otp.setVerified(false);
        otp.setUsed(false);
        otp.setExpiryTime(LocalDateTime.now()
                .plusMinutes(5));
    }

    @Test
    void shouldSendRegistrationOtpSuccessfully() {

        when(userRepository.existsByEmail("customer@gmail.com")).thenReturn(false);

        otpService.sendOtp("customer@gmail.com", OtpPurpose.REGISTRATION);

        verify(otpRepository).deleteByEmailAndPurpose("customer@gmail.com", OtpPurpose.REGISTRATION);

        verify(otpRepository).save(any(OtpVerification.class));

        verify(emailService).sendOtpEmail(org.mockito.ArgumentMatchers.eq("customer@gmail.com"), any(String.class));
    }

    @Test
    void shouldRejectRegistrationOtpWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail("customer@gmail.com")).thenReturn(true);

        UserException exception = assertThrows(UserException.class, () -> otpService.sendOtp("customer@gmail.com", OtpPurpose.REGISTRATION));

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void shouldVerifyValidOtpSuccessfully() {

        VerifyOtpRequest request = new VerifyOtpRequest();

        request.setEmail("customer@gmail.com");
        request.setOtp("123456");

        when(otpRepository.findByEmailAndOtpAndPurpose("customer@gmail.com", "123456", OtpPurpose.REGISTRATION)).thenReturn(Optional.of(otp));

        otpService.verifyOtp(request, OtpPurpose.REGISTRATION);

        assertTrue(otp.getVerified());

        verify(otpRepository).save(otp);
    }

    @Test
    void shouldRejectInvalidOtp() {

        VerifyOtpRequest request = new VerifyOtpRequest();

        request.setEmail("customer@gmail.com");
        request.setOtp("999999");

        when(otpRepository.findByEmailAndOtpAndPurpose("customer@gmail.com", "999999", OtpPurpose.REGISTRATION)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> otpService.verifyOtp(request, OtpPurpose.REGISTRATION));

        assertEquals("Invalid OTP", exception.getMessage());
    }

    @Test
    void shouldRejectAlreadyUsedOtp() {

        otp.setUsed(true);

        VerifyOtpRequest request = new VerifyOtpRequest();

        request.setEmail("customer@gmail.com");
        request.setOtp("123456");

        when(otpRepository.findByEmailAndOtpAndPurpose("customer@gmail.com", "123456", OtpPurpose.REGISTRATION)).thenReturn(Optional.of(otp));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> otpService.verifyOtp(request, OtpPurpose.REGISTRATION));

        assertEquals("OTP already used", exception.getMessage());
    }

    @Test
    void shouldRejectExpiredOtp() {

        otp.setExpiryTime(LocalDateTime.now()
                .minusMinutes(1));

        VerifyOtpRequest request = new VerifyOtpRequest();

        request.setEmail("customer@gmail.com");
        request.setOtp("123456");

        when(otpRepository.findByEmailAndOtpAndPurpose("customer@gmail.com", "123456", OtpPurpose.REGISTRATION)).thenReturn(Optional.of(otp));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> otpService.verifyOtp(request, OtpPurpose.REGISTRATION));

        assertEquals("OTP expired", exception.getMessage());
    }
}