package com.example.bankcards.controller;


import com.example.bankcards.dto.*;
import com.example.bankcards.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/createCard")
    public ResponseEntity<CardDTO> createCard(@RequestBody CardDTO cardDTO) {
        CardDTO card = adminService.saveCard(cardDTO);
        if (card == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else
            return ResponseEntity.status(HttpStatus.CREATED).body(card);

    }

    @GetMapping("/getCardById")
    public ResponseEntity<CardDTOWithoutUser> getCardById(@RequestParam UUID id) {
        return ResponseEntity.ok(adminService.findByIdCard(id));
    }

    @PutMapping("/updateCard")
    public ResponseEntity<CardDTOUpdate> updateCard(@RequestBody CardDTO cardDTO) {
        return ResponseEntity.ok(adminService.updateCard(cardDTO));
    }

    @DeleteMapping("/deleteCard")
    public ResponseEntity<String> deleteCard(@RequestParam UUID id) {
        return ResponseEntity.ok(adminService.deleteCard(id));
    }

    @GetMapping("/getAllCardsByUser")
    public ResponseEntity<List<CardDTOWithoutUser>> getAllCardsByUser(@RequestParam UUID id) {
        return ResponseEntity.ok(adminService.findAllCards(id));
    }

    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(adminService.saveUser(userDTO));
    }

    @GetMapping("/findUserById")
    public ResponseEntity<UserDTO> findUserById(@RequestParam UUID id) {
        return ResponseEntity.ok(adminService.findByIdUser(id));
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(adminService.updateUser(userDTO));
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam UUID id) {
        return ResponseEntity.ok(adminService.deleteUser(id));
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.findAllUsers());
    }

    @GetMapping("/getAllTransactionsByUser")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsByUser(@RequestParam UUID id) {
        return ResponseEntity.ofNullable(adminService.findAllTransactionsByUser(id));
    }

    @PostMapping("/approveRequestOfBlocked")
    public ResponseEntity<String> approveRequest(@RequestParam UUID userId,
                                                 @RequestParam UUID cardId,
                                                 @RequestParam boolean request) {
        return ResponseEntity.ok(adminService.approveRequestOfBlocked(userId, cardId, request));
    }

}
