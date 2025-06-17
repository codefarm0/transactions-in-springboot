package com.codefarm.usersignup.repository;

import com.codefarm.usersignup.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByEmailAndOtpAndUsedFalse(String email, String otp);
} 