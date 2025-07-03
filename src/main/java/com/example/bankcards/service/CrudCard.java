package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardDTOUpdate;
import com.example.bankcards.dto.CardDTOWithoutUser;
import java.util.List;
import java.util.UUID;

public interface CrudCard {

    CardDTO saveCard(CardDTO card);
    CardDTOWithoutUser findByIdCard(UUID id);
    CardDTOUpdate updateCard(CardDTO card);
    String deleteCard(UUID id);
    List<CardDTOWithoutUser> findAllCards(UUID id);
}
