package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardDTOWithoutUser;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.CantSaveUserException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.*;
import com.example.bankcards.util.CryptPassword;
import com.example.bankcards.util.EncryptionUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;
    @Mock
    private EncryptionUtil encryptionUtil;

    @Mock
    private CryptPassword cryptPassword;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cryptPassword.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    }

    @Test
    public void saveCard_shouldSaveCardSuccessfully() {
        UUID userId = UUID.randomUUID();
        CardDTO cardDTO = new CardDTO();
        cardDTO.setUserId(userId);
        cardDTO.setCardNumber("1234 5678");
        cardDTO.setBalance(BigDecimal.TEN);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(UserMapper.toEntity(userDTO)));

        when(encryptionUtil.encrypt("1234 5678")).thenReturn("encryptedCardNumber");
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardDTO savedCard = adminService.saveCard(cardDTO);
        assertNotNull(savedCard);
        verify(cardRepository).save(any(Card.class));
        assertEquals("encryptedCardNumber", savedCard.getCardNumber());
    }

    @Test
    public void findByIdUser_shouldThrowIfUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.findByIdUser(id));
    }

    @Test
    public void saveUser_shouldThrowIfDuplicate() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID());
        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        assertThrows(CantSaveUserException.class, () -> adminService.saveUser(userDTO));
    }

    @Test
    public void approveRequestOfBlocked_shouldApprove() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        Card card = new Card(); card.setId(cardId);
        CardBlockRequest request = new CardBlockRequest();
        request.setStatus(BlockRequestStatus.PENDING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardBlockRequestRepository.findByCardIdAndUserId(cardId, userId)).thenReturn(Optional.of(request));

        String result = adminService.approveRequestOfBlocked(userId, cardId, true);
        assertEquals("Запрос был подтвержден и выполнен", result);
        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @Test
    public void findByIdCard_shouldReturnCardDTO() {
        UUID cardId = UUID.randomUUID();
        Card card = new Card();
        card.setId(cardId);
        card.setCardNumber("encryptedCardNumber");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(encryptionUtil.decrypt("encryptedCardNumber")).thenReturn("1234 5678");
        when(encryptionUtil.maskCardNumber("1234 5678")).thenReturn("**** 5678");

        CardDTOWithoutUser result = adminService.findByIdCard(cardId);

        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals("**** 5678", result.getCardNumber());
    }


    @Test
    public void findByIdCard_shouldThrowIfCardNotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> adminService.findByIdCard(cardId));
    }

    @Test
    public void approveRequestOfBlocked_shouldRejectRequest() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        Card card = new Card(); card.setId(cardId);
        CardBlockRequest request = new CardBlockRequest();
        request.setStatus(BlockRequestStatus.APPROVED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardBlockRequestRepository.findByCardIdAndUserId(cardId, userId)).thenReturn(Optional.of(request));

        String result = adminService.approveRequestOfBlocked(userId, cardId, false);
        assertEquals("Вам отказано в блокировке карты", result);
        assertEquals(BlockRequestStatus.REJECTED, request.getStatus());
    }
}
