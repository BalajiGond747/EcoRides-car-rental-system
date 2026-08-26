package com.ecorides.controller;

import com.ecorides.ouath2.OAuth2LoginCodeService;
import com.ecorides.payload.request.LoginRequest;
import com.ecorides.payload.request.UserRegisterRequest;
import com.ecorides.payload.response.AuthResponse;
import com.ecorides.security.CustomUserDetailsService;
import com.ecorides.security.JwtAuthenticationFilter;
import com.ecorides.security.JwtUtil;
import com.ecorides.service.AuthService;
import com.ecorides.service.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private OtpService otpService;

    @MockitoBean
    private OAuth2LoginCodeService oauth2LoginCodeService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("customer@gmail.com");
        request.setPassword("Password@123");

        AuthResponse response = AuthResponse.builder()
                .userId(1L)
                .email("customer@gmail.com")
                .token("test-jwt-token")
                .tokenType("Bearer")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.email").value("customer@gmail.com"))
                .andExpect(jsonPath("$.data.token").value("test-jwt-token"));
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {

        UserRegisterRequest request = new UserRegisterRequest();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("customer@gmail.com");
        request.setPhone("9876543210");
        request.setAddress("Hitech City, Hyderabad, Telangana");
        request.setPassword("Password@123");
        request.setConfirmPassword("Password@123");

        AuthResponse response = AuthResponse.builder()
                .userId(1L)
                .email("customer@gmail.com")
                .build();

        when(authService.register(any(UserRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.data.email").value("customer@gmail.com"));
    }

    @Test
    void shouldSendRegistrationOtpSuccessfully() throws Exception {

        String request = """
                {
                    "email": "customer@gmail.com"
                }
                """;

        mockMvc.perform(post("/api/auth/send-registration-otp").contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OTP sent to your email"));
    }

    @Test
    void shouldVerifyRegistrationOtpSuccessfully() throws Exception {

        String request = """
                {
                    "email": "customer@gmail.com",
                    "otp": "123456"
                }
                """;

        mockMvc.perform(post("/api/auth/verify-registration-otp").contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email verified successfully"));
    }
}