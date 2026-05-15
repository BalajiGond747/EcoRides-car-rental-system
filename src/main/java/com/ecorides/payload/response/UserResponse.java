package com.ecorides.payload.response;

import com.ecorides.domain.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private UserRole userRole;
}