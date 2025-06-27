package com.example.bankcards.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String fullName;
    private String password;
}
