package com.carrent.carrental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Enter a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Enter a valid 10-digit contact number")
    private String contactNo;

    @NotBlank(message = "Address cannot be empty")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String address;

    @NotBlank(message = "User role is required")
    private String userRole;
}
