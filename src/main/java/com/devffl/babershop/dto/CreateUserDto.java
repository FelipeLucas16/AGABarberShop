package com.devffl.babershop.dto;

import com.devffl.babershop.enums.RoleName;

public record CreateUserDto(
        String email,
        String password,
        RoleName role) {
}
