package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTOWithoutUser;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.NotEnoughBalanceException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private UserService userService;

    private final UUID userId = UUID.randomUUID();
    private final UUID cardIdFrom = UUID.randomUUID();
    private final UUID cardIdTo = UUID.randomUUID();

    private User user;
    private Card cardFrom;
    private Card cardTo;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        cardFrom = new Card();
        cardFrom.setId(cardIdFrom);
        cardFrom.setUser(user);
        cardFrom.setBalance(BigDecimal.valueOf(1000));

        cardTo = new Card();
        cardTo.setId(cardIdTo);
        cardTo.setUser(user);
        cardTo.setBalance(BigDecimal.valueOf(500));
    }

    @Test
    void testGetAllCardsByUser() {
        Card card = new Card();
        card.setUser(user);
        card.setCardNumber("ENCRYPTED123");
        card.setId(UUID.randomUUID());

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(cardRepository.findByUser(user)).thenReturn(List.of(card));
        Mockito.when(encryptionUtil.decrypt(Mockito.anyString())).thenReturn("1234567812345678");
        Mockito.when(encryptionUtil.maskCardNumber("1234567812345678")).thenReturn("1234 **** **** 5678");

        List<CardDTOWithoutUser> result = userService.getAllCardsByUser(userId);

        assertEquals(1, result.size());
        assertEquals("1234 **** **** 5678", result.get(0).getCardNumber());
    }

    @Test
    void testRequestCardBlock() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(cardRepository.findById(cardIdFrom)).thenReturn(Optional.of(cardFrom));

        String result = userService.requestCardBlock(userId, cardIdFrom, "Lost card");

        assertEquals("Request create", result);
        Mockito.verify(cardBlockRequestRepository).save(Mockito.any(CardBlockRequest.class));
    }

    @Test
    void testMoneyTransfer_Success() {
        BigDecimal amount = BigDecimal.valueOf(200);

        Mockito.when(cardRepository.findById(cardIdFrom)).thenReturn(Optional.of(cardFrom));
        Mockito.when(cardRepository.findById(cardIdTo)).thenReturn(Optional.of(cardTo));

        String result = userService.moneyTransfer(cardIdFrom, cardIdTo, amount);

        assertEquals("Перевод выполнен успешно", result);
        assertEquals(BigDecimal.valueOf(800), cardFrom.getBalance());
        assertEquals(BigDecimal.valueOf(700), cardTo.getBalance());

        Mockito.verify(transactionRepository).save(Mockito.any(Transaction.class));
    }

    @Test
    void testMoneyTransfer_NotEnoughBalance() {
        BigDecimal amount = BigDecimal.valueOf(2000);

        Mockito.when(cardRepository.findById(cardIdFrom)).thenReturn(Optional.of(cardFrom));
        Mockito.when(cardRepository.findById(cardIdTo)).thenReturn(Optional.of(cardTo));

        assertThrows(NotEnoughBalanceException.class,
                () -> userService.moneyTransfer(cardIdFrom, cardIdTo, amount));
    }

    @Test
    void testCheckBalance_Success() {
        cardFrom.setBalance(BigDecimal.valueOf(900));
        Mockito.when(cardRepository.findById(cardIdFrom)).thenReturn(Optional.of(cardFrom));

        String balance = userService.checkBalance(userId, cardIdFrom);

        assertEquals("900", balance);
    }

    @Test
    void testCheckBalance_InvalidUser() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        cardFrom.setUser(anotherUser);

        Mockito.when(cardRepository.findById(cardIdFrom)).thenReturn(Optional.of(cardFrom));

        String result = userService.checkBalance(userId, cardIdFrom);

        assertEquals("Card not found with id: " + cardIdFrom, result);
    }

    @Test
    void testGetUserOrThrow_WhenUserNotFound() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getAllCardsByUser(userId));
    }

    @Test
    void testGetCardOrThrow_WhenCardNotFound() {
        Mockito.when(cardRepository.findById(cardIdFrom)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> userService.moneyTransfer(cardIdFrom, cardIdTo, BigDecimal.TEN));
    }
}
