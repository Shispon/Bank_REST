package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.CantSaveUserException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.RequestNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CryptPassword;
import com.example.bankcards.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервисный класс для работы с пользователями и картами.
 * Реализует интерфейсы CrudCard и CrudUser для управления данными.
 */
@Service
@RequiredArgsConstructor
public class AdminService implements CrudCard, CrudUser {

    // Репозитории для доступа к базе данных
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final CardBlockRequestRepository cardBlockRequestRepository;
    private final TransactionRepository transactionRepository;
    private final CryptPassword cryptPassword;
    private final EncryptionUtil encryptionUtil;

    /**
     * Сохраняет новую карту и привязывает её к пользователю.
     *
     * @param cardDTO объект карты с информацией
     * @return сохранённая карта
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @Override
    @Transactional
    public CardDTO saveCard(CardDTO cardDTO) {
        UserDTO userDTO = findByIdUser(cardDTO.getUserId());
        if (userDTO == null) {
            throw new UserNotFoundException(cardDTO.getUserId());
        }
        Card card = CardMapper.toEntity(cardDTO);
        card.setUser(UserMapper.toEntity(userDTO));
        String crypt = cardDTO.getCardNumber();
        crypt = encryptionUtil.encrypt(crypt);
        crypt = encryptionUtil.encrypt(crypt);
        card.setCardNumber(crypt);
        cardRepository.save(card);
        return CardMapper.toDTO(card);
    }

    /**
     * Ищет карту по ID без привязки к пользователю.
     *
     * @param id UUID карты
     * @return информация о карте без пользователя
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    @Transactional(readOnly = true)
    public CardDTOWithoutUser findByIdCard(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        String pass = encryptionUtil.decrypt(card.getCardNumber());
        pass = encryptionUtil.maskCardNumber(pass);
        card.setCardNumber(pass);
        return CardMapper.toDTOWithoutUser(card);
    }

    /**
     * Обновляет данные существующей карты.
     *
     * @param cardDTO новая информация о карте
     * @return обновлённая карта
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    @Transactional
    public CardDTOUpdate updateCard(CardDTO cardDTO) {
        Card card = cardRepository.findById(cardDTO.getId())
                .orElseThrow(() -> new CardNotFoundException(cardDTO.getId()));
        card.setCardNumber(encryptionUtil.encrypt(cardDTO.getCardNumber()));
        card.setExpiration(cardDTO.getExpiration());
        card.setStatus(cardDTO.getStatus());
        card.setBalance(cardDTO.getBalance());
        cardRepository.save(card);
        String pass = encryptionUtil.decrypt(card.getCardNumber());
        pass = encryptionUtil.maskCardNumber(pass);
        card.setCardNumber(pass);
        return CardMapper.toDTOUpdate(card);
    }

    /**
     * Удаляет карту по ID.
     *
     * @param id UUID карты
     * @return сообщение об успешном удалении
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    @Transactional
    public String deleteCard(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        cardRepository.delete(card);
        return "Карта удалена";
    }

    /**
     * Возвращает список всех карт пользователя.
     *
     * @param id UUID пользователя
     * @return список карт без данных о пользователе
     */
    @Override
    @Transactional(readOnly = true)
    public List<CardDTOWithoutUser> findAllCards(UUID id) {
        List<CardDTOWithoutUser> dtos = cardRepository.findByUser_Id(id)
                .stream()
                .map(CardMapper::toDTOWithoutUser)
                .toList();
        for(CardDTOWithoutUser dto : dtos) {
            String pass = encryptionUtil.decrypt(dto.getCardNumber());
            pass = encryptionUtil.maskCardNumber(pass);
            dto.setCardNumber(pass);
        }
        return dtos;
    }

    /**
     * Сохраняет нового пользователя в базу данных.
     *
     * @param userDTO данные пользователя
     * @return сохранённый пользователь
     * @throws CantSaveUserException если пользователь не был сохранён
     */
    @Override
    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(cryptPassword.passwordEncoder().encode(userDTO.getPassword()));
        try {
            User savedUser = userRepository.save(user);
            if (savedUser == null) {
                throw new CantSaveUserException();
            }
            return UserMapper.toUserDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new CantSaveUserException();
        }
    }

    /**
     * Ищет пользователя по ID.
     *
     * @param id UUID пользователя
     * @return данные пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    @Transactional(readOnly = true)
    public UserDTO findByIdUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toUserDTO(user);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param userDTO новая информация о пользователе
     * @return обновлённый пользователь
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UserNotFoundException(userDTO.getId()));
        user.setUserName(userDTO.getUserName());
        user.setMail(userDTO.getMail());
        user.setPassword(cryptPassword.passwordEncoder().encode(userDTO.getPassword()));
        user.setFullName(userDTO.getFullName());
        user.setActive(userDTO.isActive());

        if (userDTO.getRoles() != null) {
            Set<Role> roles = userDTO.getRoles().stream()
                    .map(roleId -> {
                        Role role = new Role();
                        role.setId(roleId);
                        return role;
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        return UserMapper.toUserDTO(user);
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id UUID пользователя
     * @return сообщение об успешном удалении
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public String deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
        return "Пользователь удален";
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей в системе
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDTO)
                .toList();
    }

    /**
     * Возвращает список всех транзакций конкретного пользователя.
     *
     * @param id UUID пользователя
     * @return список транзакций пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    public List<TransactionDTO> findAllTransactionsByUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return transactionRepository.findAllByUser_Id(user.getId()).stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }

    /**
     * Подтверждает или отклоняет запрос на блокировку карты.
     *
     * @param userId ID пользователя
     * @param cardId ID карты
     * @param request true — подтвердить, false — отклонить
     * @return сообщение о результате
     * @throws UserNotFoundException если пользователь не найден
     * @throws CardNotFoundException если карта не найдена
     * @throws RequestNotFoundException если запрос на блокировку не найден
     */
    public String approveRequestOfBlocked(UUID userId, UUID cardId, boolean request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException(cardId));
        CardBlockRequest blockRequest = cardBlockRequestRepository
                .findByCardIdAndUserId(card.getId(), user.getId())
                .orElseThrow(RequestNotFoundException::new);

        if (request) {
            blockRequest.setStatus(BlockRequestStatus.APPROVED);
            card.setStatus(CardStatus.BLOCKED);
            cardRepository.save(card);
            return "Запрос был подтвержден и выполнен";
        } else {
            blockRequest.setStatus(BlockRequestStatus.REJECTED);
            return "Вам отказано в блокировке карты";
        }
    }
}

