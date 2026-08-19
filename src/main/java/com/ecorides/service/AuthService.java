package com.ecorides.service;

import com.ecorides.payload.request.*;
import com.ecorides.payload.response.AuthResponse;

public interface AuthService {

    AuthResponse register(UserRegisterRequest request);

    AuthResponse login(LoginRequest request);

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequest request);

    void updatePassword(PasswordUpdateRequest request);

    void sendEmailChangeOtp(EmailChangeRequest request);

    void verifyEmailChangeOtp(VerifyOtpRequest request);

    void changeEmail(EmailChangeRequest request);
}