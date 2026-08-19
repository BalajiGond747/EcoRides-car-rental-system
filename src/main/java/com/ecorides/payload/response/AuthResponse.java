package com.ecorides.payload.response;

import com.ecorides.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;

    private String tokenType = "Bearer";

    private Long userId;

    private String email;

    private UserRole userRole;
}