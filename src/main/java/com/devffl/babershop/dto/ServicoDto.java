package com.devffl.babershop.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoDto {

    private Long id;
    private String nome;
    private Double preco;
    private String descricao;
}