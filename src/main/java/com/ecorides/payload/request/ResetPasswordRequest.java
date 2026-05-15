package com.ecorides.payload.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String phone;

    private String newPassword;

    private String confirmPassword;
}