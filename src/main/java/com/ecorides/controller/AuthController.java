package com.ecorides.controller;

import com.ecorides.domain.OtpPurpose;
import com.ecorides.ouath2.OAuth2LoginCode;
import com.ecorides.ouath2.OAuth2LoginCodeService;
import com.ecorides.payload.request.*;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.payload.response.AuthResponse;
import com.ecorides.security.JwtUtil;
import com.ecorides.service.AuthService;
import com.ecorides.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final OAuth2LoginCodeService oauth2LoginCodeService;
    private final JwtUtil jwtUtil;

    @PostMapping("/send-registration-otp")
    public ResponseEntity<ApiResponse<Void>> sendRegistrationOtp(@Valid @RequestBody SendOtpRequest request) {

        otpService.sendOtp(request.getEmail(), OtpPurpose.REGISTRATION);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("OTP sent to your email")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/verify-registration-otp")
    public ResponseEntity<ApiResponse<Void>> verifyRegistrationOtp(@Valid @RequestBody VerifyOtpRequest request) {

        otpService.verifyOtp(request, OtpPurpose.REGISTRATION);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Email verified successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody UserRegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Registration successful")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password reset OTP sent to your email")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse<Void>> verifyResetOtp(@Valid @RequestBody VerifyOtpRequest request) {

        otpService.verifyOtp(request, OtpPurpose.PASSWORD_RESET);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("OTP verified successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password reset successful")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PatchMapping("/update-password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {

        authService.updatePassword(request);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password updated successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/send-email-change-otp")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> sendEmailChangeOtp(@Valid @RequestBody EmailChangeRequest request) {

        authService.sendEmailChangeOtp(request);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("OTP sent to your new email")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/verify-email-change-otp")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> verifyEmailChangeOtp(@Valid @RequestBody VerifyOtpRequest request) {

        authService.verifyEmailChangeOtp(request);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("New email verified successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PatchMapping("/change-email")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeEmail(@Valid @RequestBody EmailChangeRequest request) {

        authService.changeEmail(request);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Email updated successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/oauth2/exchange")
    public ResponseEntity<ApiResponse<AuthResponse>> exchangeOAuthCode(@RequestParam String code) {

        OAuth2LoginCode loginCode = oauth2LoginCodeService.consumeCode(code);

        String token = jwtUtil.generateToken(loginCode.email());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .userId(loginCode.userId())
                .email(loginCode.email())
                .userRole(loginCode.userRole())
                .build();

        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Google login successful")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }
}