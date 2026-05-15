package com.ecorides.service;

import com.ecorides.payload.request.LoginRequest;
import com.ecorides.payload.request.ResetPasswordRequest;
import com.ecorides.payload.request.UserRegisterRequest;
import com.ecorides.payload.response.AuthResponse;

public interface AuthService {

    AuthResponse register(UserRegisterRequest request);

    AuthResponse login(LoginRequest request);

    void forgotPassword(String phone);

    void resetPassword(ResetPasswordRequest request);
}