package com.example.bankcards.dto;

import com.example.bankcards.entity.BlockRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CardBlockRequestDTO {
    private UUID id;
    private UUID cardId;
    private UUID userId;
    private String reason;
    private BlockRequestStatus status;
    private LocalDateTime createdAt;
}

