package com.devffl.babershop.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RoleName {
    ROLE_COMUM,
    ROLE_ADMINISTRADOR;

    @JsonCreator
    public static RoleName fromValue(String value) {
        return switch (value.toUpperCase()) {
            case "COMUM", "ROLE_COMUM" -> ROLE_COMUM;
            case "ADMINISTRADOR", "ROLE_ADMINISTRADOR" -> ROLE_ADMINISTRADOR;
            default -> throw new IllegalArgumentException("Role inválida: " + value);
        };
    }
}