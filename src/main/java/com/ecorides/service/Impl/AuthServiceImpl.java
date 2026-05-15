package com.ecorides.service.Impl;


import com.ecorides.domain.UserRole;
import com.ecorides.entity.User;
import com.ecorides.exception.UserException;
import com.ecorides.payload.request.LoginRequest;
import com.ecorides.payload.request.ResetPasswordRequest;
import com.ecorides.payload.request.UserRegisterRequest;
import com.ecorides.payload.response.AuthResponse;
import com.ecorides.repository.UserRepository;
import com.ecorides.security.JwtUtil;
import com.ecorides.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Override
    public AuthResponse register(UserRegisterRequest request) {

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new UserException("Phone already registered", 409);
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setUserRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);
        user.setIsVerified(true);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getPhone());

        return AuthResponse.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .token(token)
                .tokenType("Bearer")
                .build();
    }


    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new UserException("Invalid phone or password", 401));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException("Invalid phone or password", 401);
        }

        String token = jwtUtil.generateToken(user.getPhone());

        return AuthResponse.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .token(token)
                .tokenType("Bearer")
                .build();
    }


    @Override
    public void forgotPassword(String phone) {

        userRepository.findByPhone(phone)
                .orElseThrow(() -> new UserException("User not found", 404));


    }


    @Override
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new UserException("User not found", 404));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new UserException("Passwords do not match", 400);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}