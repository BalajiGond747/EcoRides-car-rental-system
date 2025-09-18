package com.carrent.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // ← required for `new LoginResponseDTO(token)`
public class LoginResponseDTO {
    
    private String token;
    private String role;
    private String email;
}
