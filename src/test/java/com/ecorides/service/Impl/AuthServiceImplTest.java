package com.ecorides.service.Impl;

import com.ecorides.domain.AuthProvider;
import com.ecorides.domain.OtpPurpose;
import com.ecorides.domain.UserRole;
import com.ecorides.entity.OtpVerification;
import com.ecorides.entity.User;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.exception.UserException;
import com.ecorides.payload.request.LoginRequest;
import com.ecorides.payload.request.ResetPasswordRequest;
import com.ecorides.payload.request.UserRegisterRequest;
import com.ecorides.payload.response.AuthResponse;
import com.ecorides.repository.OtpVerificationRepository;
import com.ecorides.repository.UserRepository;
import com.ecorides.security.JwtUtil;
import com.ecorides.service.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private OtpService otpService;

    @Mock
    private OtpVerificationRepository otpRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private OtpVerification otp;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("customer@gmail.com");
        user.setUserRole(UserRole.USER);
        user.setIsActive(true);
        user.setIsVerified(true);
        user.setProvider(AuthProvider.LOCAL);
        user.setPassword("encoded-old-password");

        otp = new OtpVerification();
        otp.setEmail("customer@gmail.com");
        otp.setPurpose(OtpPurpose.REGISTRATION);
        otp.setVerified(true);
        otp.setUsed(false);
        otp.setExpiryTime(LocalDateTime.now()
                .plusMinutes(10));
    }

    @Test
    void shouldRegisterUserAfterSuccessfulOtpVerification() {

        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("customer@gmail.com");
        request.setPassword("Password@123");

        when(otpRepository.findByEmailAndPurpose("customer@gmail.com", OtpPurpose.REGISTRATION)).thenReturn(Optional.of(otp));

        when(userRepository.existsByEmail("customer@gmail.com")).thenReturn(false);

        when(passwordEncoder.encode("Password@123")).thenReturn("encoded-password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        AuthResponse result = authService.register(request);

        assertEquals(1L, result.getUserId());
        assertEquals("customer@gmail.com", result.getEmail());
        assertEquals(UserRole.USER, result.getUserRole());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("encoded-password", savedUser.getPassword());

        assertEquals(UserRole.USER, savedUser.getUserRole());

        assertTrue(savedUser.getIsActive());
        assertTrue(savedUser.getIsVerified());
        assertEquals(AuthProvider.LOCAL, savedUser.getProvider());

        assertTrue(otp.getUsed());

        verify(otpRepository).save(otp);
    }

    @Test
    void shouldRejectRegistrationWhenOtpIsNotVerified() {

        otp.setVerified(false);

        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("customer@gmail.com");
        request.setPassword("Password@123");

        when(otpRepository.findByEmailAndPurpose("customer@gmail.com", OtpPurpose.REGISTRATION)).thenReturn(Optional.of(otp));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.register(request));

        assertEquals("Please verify your email before registration", exception.getMessage());
    }

    @Test
    void shouldRejectDuplicateEmailDuringRegistration() {

        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("customer@gmail.com");
        request.setPassword("Password@123");

        when(otpRepository.findByEmailAndPurpose("customer@gmail.com", OtpPurpose.REGISTRATION)).thenReturn(Optional.of(otp));

        when(userRepository.existsByEmail("customer@gmail.com")).thenReturn(true);

        UserException exception = assertThrows(UserException.class, () -> authService.register(request));

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setEmail("customer@gmail.com");
        request.setPassword("Password@123");

        when(userRepository.findByEmail("customer@gmail.com")).thenReturn(Optional.of(user));

        when(jwtUtil.generateToken("customer@gmail.com")).thenReturn("test-jwt-token");

        AuthResponse result = authService.login(request);

        assertEquals(1L, result.getUserId());
        assertEquals("customer@gmail.com", result.getEmail());
        assertEquals(UserRole.USER, result.getUserRole());
        assertEquals("test-jwt-token", result.getToken());
        assertEquals("Bearer", result.getTokenType());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldRejectForgotPasswordForUnknownUser() {

        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> authService.forgotPassword("unknown@gmail.com"));

        assertEquals("User not found with email: unknown@gmail.com", exception.getMessage());
    }

    @Test
    void shouldRejectResetPasswordWhenPasswordsDoNotMatch() {

        ResetPasswordRequest request = new ResetPasswordRequest();

        request.setEmail("customer@gmail.com");
        request.setNewPassword("NewPassword@123");
        request.setConfirmPassword("DifferentPassword@123");

        OtpVerification resetOtp = new OtpVerification();

        resetOtp.setEmail("customer@gmail.com");
        resetOtp.setPurpose(OtpPurpose.PASSWORD_RESET);
        resetOtp.setVerified(true);
        resetOtp.setUsed(false);

        when(userRepository.findByEmail("customer@gmail.com")).thenReturn(Optional.of(user));

        when(otpRepository.findByEmailAndPurpose("customer@gmail.com", OtpPurpose.PASSWORD_RESET)).thenReturn(Optional.of(resetOtp));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.resetPassword(request));

        assertEquals("Passwords do not match", exception.getMessage());
    }
}