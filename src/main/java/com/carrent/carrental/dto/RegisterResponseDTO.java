package com.carrent.carrental.dto;


import com.carrent.carrental.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // ← this is needed for `new RegisterResponseDTO(...)`
public class RegisterResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNo;
    private String address;
    private UserRole userRole;
}
