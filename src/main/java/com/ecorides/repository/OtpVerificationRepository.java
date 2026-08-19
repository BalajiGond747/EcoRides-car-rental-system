package com.ecorides.repository;

import com.ecorides.domain.OtpPurpose;
import com.ecorides.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByEmailAndPurpose(String email, OtpPurpose purpose);

    Optional<OtpVerification> findByEmailAndOtpAndPurpose(String email, String otp, OtpPurpose purpose);

    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
}