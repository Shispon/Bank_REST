package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CardDTOUpdate {
    private UUID id;
    private String cardNumber;
    private LocalDate expiration;
    private CardStatus status;
    private BigDecimal balance;
}
