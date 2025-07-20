package com.devffl.babershop.dto;

import com.devffl.babershop.enums.RoleName;

public record CreateUserDto(
        String nome,
        String telefone,
        String email,
        String password,
        RoleName role) {
}
