package com.codefarm.usersignup.service;

import com.codefarm.usersignup.dto.UserResponse;
import com.codefarm.usersignup.dto.UserSignupRequest;
import com.codefarm.usersignup.dto.OtpVerificationRequest;
import com.codefarm.usersignup.dto.UserProfileResponse;
import com.codefarm.usersignup.model.User;
import com.codefarm.usersignup.model.OtpVerification;
import com.codefarm.usersignup.repository.UserRepository;
import com.codefarm.usersignup.repository.OtpVerificationRepository;
import com.codefarm.usersignup.exception.UserNotFoundException;
import com.codefarm.usersignup.exception.OtpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuditService auditService;
    private final OtpVerificationRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    
    @Transactional
    public UserResponse signupUser(UserSignupRequest request) {
        log.info("Processing signup request for user: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new OtpException("User with this email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Save user
        user = userRepository.save(user);
        log.info("User saved successfully with id: {}", user.getId());
        
        try {
            // Send OTP email in a new transaction
            emailService.sendOtpEmail(user);
            
            // Log audit event in a new transaction
            auditService.logAuditEvent(user);
            
            // Convert to response
            return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .status(user.getStatus())
                    .emailVerified(user.isEmailVerified())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in signup process", e);
            throw new OtpException("Error during signup process: " + e.getMessage());
        }
    }

    @Transactional
    public UserResponse verifyOtp(OtpVerificationRequest request) {
        log.info("Verifying OTP for user: {}", request.getEmail());


        // Find the user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));
        
        // Find the OTP verification
        boolean verification = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if(verification) {
            // Update user status
            user.setEmailVerified(true);
            user.setStatus("ACTIVE");
            user = userRepository.save(user);

            log.info("Email verified successfully for user: {}", user.getEmail());

            // Convert to response
            return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .status(user.getStatus())
                    .emailVerified(user.isEmailVerified())
                    .build();
        }{
            throw new OtpException("Otp verification failed");
        }
    }

    public UserProfileResponse getUserProfile(String email) {
        log.info("Fetching user profile for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .emailVerified(user.isEmailVerified())
                .build();
    }
} 