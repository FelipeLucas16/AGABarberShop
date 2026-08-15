package com.devffl.babershop.services;

import com.devffl.babershop.dto.OrdemServicoDto;
import com.devffl.babershop.entities.OrdemServico;
import com.devffl.babershop.enums.MetodoPagamento;
import com.devffl.babershop.enums.StatusPagamento;
import com.devffl.babershop.repositories.OrdemServicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private OrdemServicoService ordemServicoService;

    @Test
    void finalizarPagamentoMarcaOrdemComoPagaEGravaMetodo() {
        OrdemServico ordem = OrdemServico.builder().id(1L).status(StatusPagamento.ABERTO).build();

        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordem));
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoDto atualizado = ordemServicoService.finalizarPagamento(1L, MetodoPagamento.PIX);

        assertThat(atualizado.getStatus()).isEqualTo(StatusPagamento.PAGO);
        assertThat(atualizado.getMetodoPagamento()).isEqualTo(MetodoPagamento.PIX);
    }

    @Test
    void finalizarPagamentoSemMetodoLancaExcecao() {
        assertThatThrownBy(() -> ordemServicoService.finalizarPagamento(1L, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(ordemServicoRepository, never()).findById(any());
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    void finalizarPagamentoDeOrdemInexistenteLancaExcecao() {
        when(ordemServicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordemServicoService.finalizarPagamento(99L, MetodoPagamento.DINHEIRO))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
