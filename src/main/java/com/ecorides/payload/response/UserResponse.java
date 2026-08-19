package com.ecorides.payload.response;

import com.ecorides.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private UserRole userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}