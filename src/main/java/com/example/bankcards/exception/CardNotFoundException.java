package com.example.bankcards.exception;

import java.util.UUID;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(UUID cardId) {
        super("Карта с id " + cardId + " не найдена");
    }
}