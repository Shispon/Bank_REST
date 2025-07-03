package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTOWithoutUser;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.NotEnoughBalanceException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.EncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с пользователями: получение карт, запрос блокировки карты,
 * перевод денег между картами, проверка баланса.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final CardBlockRequestRepository cardBlockRequestRepository;
    private final TransactionRepository transactionRepository;
    private final EncryptionUtil encryptionUtil;

    /**
     * Получает список всех карт, принадлежащих пользователю.
     *
     * @param userId ID пользователя
     * @return список DTO карт без информации о пользователе
     * @throws UserNotFoundException если пользователь не найден
     */
    @Transactional(readOnly = true)
    public List<CardDTOWithoutUser> getAllCardsByUser(UUID userId) {
        User user = getUserOrThrow(userId);
        List<CardDTOWithoutUser> dtos = cardRepository.findByUser(user).stream()
                .map(CardMapper::toDTOWithoutUser)
                .toList();
        for (CardDTOWithoutUser dto : dtos) {
            String pass = encryptionUtil.decrypt(dto.getCardNumber());
            pass = encryptionUtil.maskCardNumber(pass);
            dto.setCardNumber(pass);
        }
        return dtos;
    }

    /**
     * Создает запрос на блокировку карты.
     *
     * @param userId ID пользователя, инициировавшего запрос
     * @param cardId ID карты, которую нужно заблокировать
     * @param reason причина блокировки
     * @return сообщение о результате создания запроса
     * @throws EntityNotFoundException если карта или пользователь не найдены
     */
    @Transactional
    public String requestCardBlock(UUID userId, UUID cardId, String reason) {
        try {
            Card card = getCardOrThrow(cardId);
            User user = getUserOrThrow(userId);

            CardBlockRequest request = new CardBlockRequest();
            request.setCard(card);
            request.setUser(user);
            request.setReason(reason);
            request.setStatus(BlockRequestStatus.PENDING);
            request.setCreatedAt(LocalDateTime.now());

            cardBlockRequestRepository.save(request);
            return "Request create";
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Card or User not found with id: " + cardId);
        }
    }

    /**
     * Выполняет перевод денег с одной карты на другую.
     *
     * @param cardFromId ID карты-отправителя
     * @param cardToId   ID карты-получателя
     * @param amount     сумма перевода
     * @return сообщение об успешном переводе
     * @throws CardNotFoundException     если одна из карт не найдена
     * @throws NotEnoughBalanceException если на карте отправителя недостаточно средств
     */
    @Transactional
    public String moneyTransfer(UUID cardFromId, UUID cardToId, BigDecimal amount) {
        Card cardFrom = getCardOrThrow(cardFromId);
        Card cardTo = getCardOrThrow(cardToId);

        if (cardFrom.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughBalanceException(cardFromId, amount);
        }

        cardFrom.setBalance(cardFrom.getBalance().subtract(amount));
        cardTo.setBalance(cardTo.getBalance().add(amount));

        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);

        Transaction transaction = new Transaction();
        transaction.setFromCard(cardFrom);
        transaction.setToCard(cardTo);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return "Перевод выполнен успешно";
    }

    /**
     * Проверяет баланс карты пользователя.
     *
     * @param userId ID пользователя
     * @param cardId ID карты
     * @return строка с балансом карты, либо сообщение об ошибке
     * @throws CardNotFoundException если карта не найдена
     */
    @Transactional(readOnly = true)
    public String checkBalance(UUID userId, UUID cardId) {
        Card card = getCardOrThrow(cardId);
        if (card.getUser().getId().equals(userId)) {
            return card.getBalance().toString();
        } else {
            return "Card not found with id: " + cardId;
        }
    }

    /**
     * Вспомогательный метод: получает пользователя по ID или выбрасывает исключение.
     *
     * @param userId ID пользователя
     * @return найденный пользователь
     * @throws UserNotFoundException если пользователь не найден
     */
    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Вспомогательный метод: получает карту по ID или выбрасывает исключение.
     *
     * @param cardId ID карты
     * @return найденная карта
     * @throws CardNotFoundException если карта не найдена
     */
    private Card getCardOrThrow(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
    }
}

