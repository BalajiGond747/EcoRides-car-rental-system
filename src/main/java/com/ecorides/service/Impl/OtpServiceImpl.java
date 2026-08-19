package com.ecorides.service.Impl;

import com.ecorides.domain.OtpPurpose;
import com.ecorides.entity.OtpVerification;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.exception.UserException;
import com.ecorides.payload.request.VerifyOtpRequest;
import com.ecorides.repository.OtpVerificationRepository;
import com.ecorides.repository.UserRepository;
import com.ecorides.service.EmailService;
import com.ecorides.service.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void sendOtp(String email, OtpPurpose purpose) {

        if (purpose == OtpPurpose.REGISTRATION) {

            if (userRepository.existsByEmail(email)) {
                throw new UserException("Email already registered");
            }
        }

        if (purpose == OtpPurpose.PASSWORD_RESET) {

            userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        }

        otpRepository.deleteByEmailAndPurpose(email, purpose);

        String otp = String.valueOf(ThreadLocalRandom.current()
                .nextInt(100_000, 1_000_000));

        OtpVerification otpEntity = new OtpVerification();

        otpEntity.setEmail(email);
        otpEntity.setPurpose(purpose);
        otpEntity.setOtp(otp);
        otpEntity.setVerified(false);
        otpEntity.setUsed(false);
        otpEntity.setExpiryTime(LocalDateTime.now()
                .plusMinutes(5));

        otpRepository.save(otpEntity);

        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public void verifyOtp(VerifyOtpRequest request, OtpPurpose purpose) {

        OtpVerification otp = otpRepository.findByEmailAndOtpAndPurpose(request.getEmail(), request.getOtp(), purpose)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (Boolean.TRUE.equals(otp.getUsed())) {
            throw new BadRequestException("OTP already used");
        }

        if (otp.getExpiryTime()
                .isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }

        otp.setVerified(true);

        otpRepository.save(otp);
    }
}