package com.devffl.babershop.dto;

import com.devffl.babershop.entities.OrdemServico;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServicoDto {
    public Long id;
    private UserDto user;
    private List<ServicoDto> servicos;
    private List<ProdutoDto> produtos;
    private Double valorTotal;

}