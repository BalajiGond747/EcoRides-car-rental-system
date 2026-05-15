package com.ecorides.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Pattern(regexp = "\\d{10}", message = "Enter a valid phone number")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;
}