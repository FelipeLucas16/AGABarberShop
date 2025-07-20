package com.devffl.babershop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    public String nome;
    private String email;
    private String telefone;
}
