package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTOWithoutUser;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getAllCardsOfUser")
    public ResponseEntity<List<CardDTOWithoutUser>> getAllCardsOfUser(@RequestParam UUID userId) {
        List<CardDTOWithoutUser> cards = userService.getAllCardsByUser(userId);
        if (cards.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/createBlockRequest")
    public ResponseEntity<String> createBlockRequest(@RequestParam UUID userId,
                                                     @RequestParam UUID cardId,
                                                     @RequestParam String reason) {
        return ResponseEntity.ofNullable(userService.requestCardBlock(userId, cardId, reason));
    }

    @PostMapping("/createTransaction")
    public ResponseEntity<String> createTransaction(@RequestParam UUID cardFromId,
                                                    @RequestParam UUID cardToId,
                                                    @RequestParam BigDecimal amount) {
        return ResponseEntity.ofNullable(userService.moneyTransfer(cardFromId, cardToId, amount));
    }

    @GetMapping("/checkBalance")
    public ResponseEntity<String> checkBalance(@RequestParam UUID userId,
                                               @RequestParam UUID cardId) {
        return ResponseEntity.ofNullable(userService.checkBalance(userId, cardId));
    }
}

