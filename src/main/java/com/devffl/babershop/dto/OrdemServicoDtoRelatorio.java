package com.devffl.babershop.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServicoDtoRelatorio {
    public Long id;
    private String user;
    private String servicos;
    private String produtos;
    private Double valorTotal;

}