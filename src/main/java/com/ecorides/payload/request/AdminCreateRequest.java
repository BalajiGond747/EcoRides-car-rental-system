package com.ecorides.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 50)
    private String lastName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 200)
    private String address;
}