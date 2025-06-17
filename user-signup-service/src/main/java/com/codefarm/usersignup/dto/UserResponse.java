package com.codefarm.usersignup.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String status;
    private boolean emailVerified;
} 