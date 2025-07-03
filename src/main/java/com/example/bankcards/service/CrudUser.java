package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface CrudUser {

    UserDTO saveUser(UserDTO user);

    UserDTO findByIdUser(UUID id);

    UserDTO updateUser(UserDTO user);

    String deleteUser(UUID id);

    List<UserDTO> findAllUsers();
}
