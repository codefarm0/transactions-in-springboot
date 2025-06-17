package com.codefarm.usersignup.service;

import com.codefarm.usersignup.model.User;
import com.codefarm.usersignup.model.OtpVerification;
import com.codefarm.usersignup.repository.OtpVerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final OtpVerificationRepository otpRepository;
    private final OtpService otpService;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendOtpEmail(User user) {
        try {
            String otp = otpService.createOtp(user.getEmail());
            // Read email template
            String emailContent = readEmailTemplate();
            
            // Replace placeholders
            emailContent = emailContent.replace("{name}", user.getName())
                                     .replace("{otp}", otp);
            
            // Create email message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("arvind@codefarm.com");
            helper.setTo(user.getEmail());
            helper.setSubject("Verify Your Email Address");
            helper.setText(emailContent, true);
            
            // Send email
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", user.getEmail());
            
        } catch (MessagingException e) {
            log.error("Error sending OTP email", e);
            throw new RuntimeException("Failed to send OTP email");
        }
    }
    
    private String readEmailTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/otp-email.html");
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return FileCopyUtils.copyToString(reader);
        } catch (Exception e) {
            log.error("Error reading email template", e);
            throw new RuntimeException("Failed to read email template");
        }
    }
} 