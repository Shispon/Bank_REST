package com.example.bankcards.mapper;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Transaction;

public class TransactionMapper {
    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {return null;}
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUser().getId());
        dto.setFromCardId(transaction.getFromCard().getId());
        dto.setToCardId(transaction.getToCard().getId());
        dto.setAmount(transaction.getAmount());
        dto.setTimestamp(transaction.getTimestamp());
        return dto;
    }
}
