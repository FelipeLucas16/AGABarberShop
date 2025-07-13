package com.devffl.babershop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDto {
    private Long id;
    private String nome;
    private Double preco;
    private String descricao;
}
