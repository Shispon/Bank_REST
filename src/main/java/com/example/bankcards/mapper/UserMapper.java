package com.example.bankcards.mapper;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserMapper {

    public static User toEntity(UserDTO userDTO) {
        if (userDTO == null) return null;

        User user = new User();
        user.setId(userDTO.getId());
        user.setMail(userDTO.getMail());
        user.setUserName(userDTO.getUserName());
        user.setPassword(userDTO.getPassword());
        user.setFullName(userDTO.getFullName());
        user.setActive(userDTO.isActive());
        user.setCreatedAt(userDTO.getCreatedAt());

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

        return user;
    }

    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setMail(user.getMail());
        userDTO.setUserName(user.getUserName());
        userDTO.setFullName(user.getFullName());
        userDTO.setPassword(user.getPassword());
        userDTO.setActive(user.isActive());
        userDTO.setCreatedAt(user.getCreatedAt());

        Set<UUID> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        userDTO.setRoles(roleIds);

        return userDTO;
    }
}
