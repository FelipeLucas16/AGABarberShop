package com.devffl.babershop.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendamentoDto {

    private Long id;
    private Long userId;
    private String userEmail;
    private LocalDateTime dataHora;
    private String status;
}