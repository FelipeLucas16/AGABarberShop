package com.devffl.babershop.dto;

import com.devffl.babershop.enums.MetodoPagamento;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalizarPagamentoDto {
    private MetodoPagamento metodoPagamento;
}
