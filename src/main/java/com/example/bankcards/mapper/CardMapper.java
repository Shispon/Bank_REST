package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardDTOUpdate;
import com.example.bankcards.dto.CardDTOWithoutUser;
import com.example.bankcards.entity.Card;

public class CardMapper {
    public static Card toEntity (CardDTO cardDTO) {
        if (cardDTO == null) {
            return null;
        }
        Card card = new Card();
        card.setId(cardDTO.getId());
        card.setCardNumber( cardDTO.getCardNumber());
        card.setExpiration( cardDTO.getExpiration());
        card.setStatus( cardDTO.getStatus());
        card.setBalance(cardDTO.getBalance());
        card.setCreatedAt( cardDTO.getCreatedAt());
        return card;
    }

    public static CardDTO toDTO (Card card) {
        if (card == null) {
            return null;
        }
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(card.getId());
        cardDTO.setCardNumber(card.getCardNumber());
        cardDTO.setExpiration(card.getExpiration());
        cardDTO.setStatus(card.getStatus());
        cardDTO.setBalance(card.getBalance());
        cardDTO.setCreatedAt(card.getCreatedAt());
        cardDTO.setUserId(card.getUser().getId());
        return cardDTO;
    }
    public static CardDTOWithoutUser toDTOWithoutUser (Card card) {
        if (card == null) {
            return null;
        }
        CardDTOWithoutUser cardDTO = new CardDTOWithoutUser();
        cardDTO.setId(card.getId());
        cardDTO.setCardNumber(card.getCardNumber());
        cardDTO.setExpiration(card.getExpiration());
        cardDTO.setStatus(card.getStatus());
        cardDTO.setBalance(card.getBalance());
        cardDTO.setCreatedAt(card.getCreatedAt());
        return cardDTO;
    }

    public static CardDTOUpdate toDTOUpdate (Card card) {
        if (card == null) {
            return null;
        }
        CardDTOUpdate cardDTO = new CardDTOUpdate();
        cardDTO.setId(card.getId());
        cardDTO.setCardNumber(card.getCardNumber());
        cardDTO.setExpiration(card.getExpiration());
        cardDTO.setStatus(card.getStatus());
        cardDTO.setBalance(card.getBalance());
        return cardDTO;
    }

}
