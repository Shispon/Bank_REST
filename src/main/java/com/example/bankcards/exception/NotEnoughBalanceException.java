package com.example.bankcards.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class NotEnoughBalanceException extends RuntimeException {
    public NotEnoughBalanceException(UUID cardId, BigDecimal amount) {
        super("Недостаточно средств на карте: " + cardId + ". Требуется: " + amount);
    }
}
