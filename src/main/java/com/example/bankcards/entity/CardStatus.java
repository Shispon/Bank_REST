package com.example.bankcards.entity;

import lombok.Getter;

@Getter
public enum CardStatus {
    ACTIVE("активна"),
    BLOCKED("заблокирована"),
    EXPIRED("просрочена"),
    CLOSED("закрыта");

    private final String description;  // описание на русском

    CardStatus(String description) {
        this.description = description;
    }

}

