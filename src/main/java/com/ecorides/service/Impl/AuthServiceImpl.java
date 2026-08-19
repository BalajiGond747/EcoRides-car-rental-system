package com.ecorides.service.Impl;

import com.ecorides.domain.AuthProvider;
import com.ecorides.domain.OtpPurpose;
import com.ecorides.domain.UserRole;
import com.ecorides.entity.OtpVerification;
import com.ecorides.entity.User;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.exception.UserException;
import com.ecorides.mappers.UserMapper;
import com.ecorides.payload.request.*;
import com.ecorides.payload.response.AuthResponse;
import com.ecorides.repository.OtpVerificationRepository;
import com.ecorides.repository.UserRepository;
import com.ecorides.security.JwtUtil;
import com.ecorides.service.AuthService;
import com.ecorides.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final OtpVerificationRepository otpRepository;

    @Override
    public AuthResponse register(UserRegisterRequest request) {

        OtpVerification otp = otpRepository.findByEmailAndPurpose(request.getEmail(), OtpPurpose.REGISTRATION)
                .orElseThrow(() -> new BadRequestException("Please verify your email before registration"));

        if (!Boolean.TRUE.equals(otp.getVerified())) {
            throw new BadRequestException("Please verify your email before registration");
        }

        if (Boolean.TRUE.equals(otp.getUsed())) {
            throw new BadRequestException("Email verification has already been used");
        }

        if (otp.getExpiryTime()
                .isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Email verification has expired");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email already registered");
        }

        User user = UserMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserRole(UserRole.USER);
        user.setIsActive(true);
        user.setIsVerified(true);
        user.setProvider(AuthProvider.LOCAL);
        userRepository.save(user);

        otp.setUsed(true);
        otpRepository.save(otp);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .token(token)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public void forgotPassword(String email) {

        userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        otpService.sendOtp(email, OtpPurpose.PASSWORD_RESET);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        OtpVerification otp = otpRepository.findByEmailAndPurpose(request.getEmail(), OtpPurpose.PASSWORD_RESET)
                .orElseThrow(() -> new BadRequestException("OTP verification required"));

        if (!Boolean.TRUE.equals(otp.getVerified())) {
            throw new BadRequestException("Please verify OTP first");
        }

        if (Boolean.TRUE.equals(otp.getUsed())) {
            throw new BadRequestException("OTP has already been used");
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new BadRequestException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        otp.setUsed(true);
        otpRepository.save(otp);
    }

    @Override
    public void updatePassword(PasswordUpdateRequest request) {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {

            throw new BadRequestException("User is not authenticated");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {

            throw new BadCredentialsException("Old password is incorrect");
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new BadRequestException("Passwords do not match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {

            throw new BadRequestException("New password must be different from old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    @Override
    public void sendEmailChangeOtp(EmailChangeRequest request) {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User is not authenticated");
        }

        String currentEmail = authentication.getName();

        String newEmail = request.getNewEmail()
                .trim()
                .toLowerCase();

        if (currentEmail.equalsIgnoreCase(newEmail)) {
            throw new BadRequestException("New email must be different from current email");
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new UserException("Email already registered");
        }

        otpService.sendOtp(newEmail, OtpPurpose.EMAIL_CHANGE);
    }

    @Override
    public void verifyEmailChangeOtp(VerifyOtpRequest request) {

        otpService.verifyOtp(request, OtpPurpose.EMAIL_CHANGE);
    }

    @Override
    public void changeEmail(EmailChangeRequest request) {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User is not authenticated");
        }

        String currentEmail = authentication.getName();

        String newEmail = request.getNewEmail()
                .trim()
                .toLowerCase();

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentEmail.equalsIgnoreCase(newEmail)) {
            throw new BadRequestException("New email must be different from current email");
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new UserException("Email already registered");
        }

        OtpVerification otp = otpRepository.findByEmailAndPurpose(newEmail, OtpPurpose.EMAIL_CHANGE)
                .orElseThrow(() -> new BadRequestException("Please verify your new email first"));

        if (!Boolean.TRUE.equals(otp.getVerified())) {
            throw new BadRequestException("Please verify your new email first");
        }

        if (Boolean.TRUE.equals(otp.getUsed())) {
            throw new BadRequestException("Email verification has already been used");
        }

        user.setEmail(newEmail);

        userRepository.save(user);

        otp.setUsed(true);
        otpRepository.save(otp);
    }
}