package com.devffl.babershop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioFinanceiroItemDto {
    private String tipo;
    private String nome;
    private Long quantidade;
    private Double valorTotal;
}
