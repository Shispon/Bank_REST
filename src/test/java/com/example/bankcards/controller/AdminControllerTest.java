package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardDTOWithoutUser;
import com.example.bankcards.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)  // инициализация Mockito
public class AdminControllerTest {

    private MockMvc mockMvc;  // имитация HTTP-запросов

    @Mock
    private AdminService adminService;  // мок сервиса

    @InjectMocks
    private AdminController adminController;  // контроллер с моками

    private ObjectMapper objectMapper = new ObjectMapper();  // для JSON

    @BeforeEach
    void setup() {
        // Создаём MockMvc для standalone-теста контроллера с моками
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    public void createCard_shouldReturnCreatedCard() throws Exception {
        UUID userId = UUID.randomUUID();
        CardDTO cardDTO = new CardDTO();
        cardDTO.setUserId(userId);
        cardDTO.setCardNumber("1234 5678");
        cardDTO.setBalance(BigDecimal.TEN);

        CardDTO savedCardDTO = new CardDTO();
        savedCardDTO.setUserId(userId);
        savedCardDTO.setCardNumber("encryptedCardNumber");
        savedCardDTO.setBalance(BigDecimal.TEN);

        when(adminService.saveCard(any(CardDTO.class))).thenReturn(savedCardDTO);

        mockMvc.perform(post("/admin/createCard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardNumber").value("encryptedCardNumber"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    public void getCardById_shouldReturnCardDTOWithoutUser() throws Exception {
        UUID cardId = UUID.randomUUID();
        CardDTOWithoutUser cardDTOWithoutUser = new CardDTOWithoutUser();
        cardDTOWithoutUser.setId(cardId);
        cardDTOWithoutUser.setCardNumber("**** 5678");

        when(adminService.findByIdCard(cardId)).thenReturn(cardDTOWithoutUser);

        mockMvc.perform(get("/admin/getCardById")
                        .param("id", cardId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.cardNumber").value("**** 5678"));
    }


}
