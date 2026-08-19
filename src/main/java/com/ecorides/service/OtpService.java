package com.ecorides.service;

import com.ecorides.domain.OtpPurpose;
import com.ecorides.payload.request.VerifyOtpRequest;

public interface OtpService {

    void sendOtp(String email, OtpPurpose purpose);

    void verifyOtp(VerifyOtpRequest request, OtpPurpose purpose);
}