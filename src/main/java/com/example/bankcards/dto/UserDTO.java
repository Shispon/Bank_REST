package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String mail;
    private String userName;
    private String password;
    private String fullName;
    private boolean isActive;
    private LocalDateTime createdAt;
    private Set<UUID> roles;
}

