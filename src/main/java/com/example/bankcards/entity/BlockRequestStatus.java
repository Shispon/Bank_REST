package com.example.bankcards.entity;

public enum BlockRequestStatus {
    PENDING("ожидает рассмотрения"),
    APPROVED("одобрено"),
    REJECTED("отклонено");

    private final String description;

    BlockRequestStatus(String description) {
        this.description = description;
    }
}
