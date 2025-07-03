package com.example.bankcards.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TransactionDTO {
    private UUID id;
    private UUID userId;
    private UUID fromCardId;
    private UUID toCardId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
