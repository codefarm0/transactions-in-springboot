package com.codefarm.usersignup.controller;

import com.codefarm.usersignup.dto.UserResponse;
import com.codefarm.usersignup.dto.UserSignupRequest;
import com.codefarm.usersignup.dto.OtpVerificationRequest;
import com.codefarm.usersignup.dto.UserProfileResponse;
import com.codefarm.usersignup.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        log.info("Received signup request for user: {}", request.getEmail());
        UserResponse response = userService.signupUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<UserResponse> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        log.info("Received OTP verification request for user: {}", request.getEmail());
        UserResponse response = userService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("Fetching profile for user: {}", email);
        UserProfileResponse profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }
} 