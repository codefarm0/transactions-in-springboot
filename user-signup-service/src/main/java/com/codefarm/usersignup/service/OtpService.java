package com.codefarm.usersignup.service;

import com.codefarm.usersignup.exception.OtpException;
import com.codefarm.usersignup.model.OtpVerification;
import com.codefarm.usersignup.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 5;

    public String createOtp(String email) {
        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES);

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setExpiryTime(expiryTime);
        otpVerification.setUsed(false);

        otpRepository.save(otpVerification);
        log.info("OTP sent to email: {}", email);
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        OtpVerification otpVerification = otpRepository.findByEmailAndOtpAndUsedFalse(email, otp)
                .orElseThrow(() -> new OtpException("Invalid OTP provided"));

        // Check if OTP is expired
        if (otpVerification.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpException("OTP has expired. Please request a new one");
        }

        // Mark OTP as used
        otpVerification.setUsed(true);
        otpRepository.save(otpVerification);

       return true;
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
} 