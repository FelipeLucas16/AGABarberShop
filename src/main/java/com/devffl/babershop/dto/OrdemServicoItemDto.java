package com.devffl.babershop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServicoItemDto {
    private String tipo;
    private String nome;
    private Double valor;
}
