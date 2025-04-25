package com.devffl.babershop.dto;

import com.devffl.babershop.entities.Role;

import java.util.List;

public record RecoveryUserDto(
        Long id,
        String email,
        List<Role> roles
) {
}
