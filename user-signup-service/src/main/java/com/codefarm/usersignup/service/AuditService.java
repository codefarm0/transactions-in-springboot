package com.codefarm.usersignup.service;

import com.codefarm.usersignup.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuditService {
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAuditEvent(User user) {
        // In a real application, this would save to an audit log table
        // For now, we'll just log it
        log.info("Audit: User signup event for user: {}", user.getEmail());
        
        // Simulate audit logging
        try {
            Thread.sleep(500); // Simulate network delay
            log.info("Audit log saved successfully for user: {}", user.getEmail());
        } catch (InterruptedException e) {
            log.error("Error saving audit log", e);
            throw new RuntimeException("Failed to save audit log");
        }
    }
} 